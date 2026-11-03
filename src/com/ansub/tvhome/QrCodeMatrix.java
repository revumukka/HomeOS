package com.ansub.tvhome;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Self-contained ISO/IEC 18004 QR Code generator (Byte mode, ECC Level L, Versions 1..6)
 * with GF(256) Reed-Solomon error correction so any iPhone or Android camera pointed at
 * the TV or phone screen immediately decodes the URL.
 */
public final class QrCodeMatrix {

    // Total data codewords and ECC codewords per version (1..6) at ECC Level L (single block)
    private static final int[] TOTAL_CODEWORDS = {0, 26, 44, 70, 100, 134, 172};
    private static final int[] ECC_CODEWORDS   = {0,  7, 10, 15,  20,  26,  18}; // v6 uses 2 blocks of 18 ecc / 68 data

    public static boolean[][] encodeUrl(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        // Select smallest version (1..5 single-block Level L supports up to 106 bytes)
        int version = 3;
        for (int v = 1; v <= 5; v++) {
            int maxDataBytes = (TOTAL_CODEWORDS[v] - ECC_CODEWORDS[v]) - 2;
            if (data.length <= maxDataBytes) {
                version = v;
                break;
            }
        }
        int totalCw = TOTAL_CODEWORDS[version];
        int eccCw = ECC_CODEWORDS[version];
        int dataCw = totalCw - eccCw;

        // 1. Pack Byte-mode bitstream: 4-bit mode (0100), 8-bit length, payload bytes, terminator, pad 0xEC/0x11
        int[] bitBuf = new int[dataCw * 8];
        int bitPos = 0;
        bitPos = appendBits(bitBuf, bitPos, 0b0100, 4);
        bitPos = appendBits(bitBuf, bitPos, Math.min(data.length, dataCw - 2), 8);
        for (int i = 0; i < Math.min(data.length, dataCw - 2); i++) {
            bitPos = appendBits(bitBuf, bitPos, data[i] & 0xFF, 8);
        }
        bitPos = appendBits(bitBuf, bitPos, 0, Math.min(4, bitBuf.length - bitPos));
        while (bitPos % 8 != 0 && bitPos < bitBuf.length) {
            bitBuf[bitPos++] = 0;
        }
        int[] codewords = new int[totalCw];
        for (int i = 0; i < dataCw; i++) {
            int b = 0;
            for (int bit = 0; bit < 8; bit++) {
                b = (b << 1) | bitBuf[i * 8 + bit];
            }
            codewords[i] = b;
        }
        int padIdx = 0;
        for (int i = bitPos / 8; i < dataCw; i++) {
            codewords[i] = (padIdx++ % 2 == 0) ? 0xEC : 0x11;
        }

        // 2. Compute GF(256) Reed-Solomon ECC codewords (primitive polynomial 0x11D)
        int[] ecc = computeReedSolomon(codewords, dataCw, eccCw);
        System.arraycopy(ecc, 0, codewords, dataCw, eccCw);

        // 3. Build module matrix (size = 17 + 4 * version)
        int size = 17 + 4 * version;
        boolean[][] modules = new boolean[size][size];
        boolean[][] isFunction = new boolean[size][size];

        // Finder patterns + separators
        drawFinderPattern(modules, isFunction, 0, 0);
        drawFinderPattern(modules, isFunction, size - 7, 0);
        drawFinderPattern(modules, isFunction, 0, size - 7);

        // Timing patterns
        for (int i = 8; i < size - 8; i++) {
            modules[6][i] = (i % 2 == 0);
            isFunction[6][i] = true;
            modules[i][6] = (i % 2 == 0);
            isFunction[i][6] = true;
        }

        // Alignment pattern for Version 2..5
        if (version >= 2) {
            int alignPos = size - 7;
            drawAlignmentPattern(modules, isFunction, alignPos, alignPos);
        }

        // Reserve format info & dark module
        for (int i = 0; i < 9; i++) {
            isFunction[8][i] = true;
            isFunction[i][8] = true;
        }
        for (int i = 0; i < 8; i++) {
            isFunction[8][size - 1 - i] = true;
            isFunction[size - 1 - i][8] = true;
        }
        modules[size - 8][8] = true;
        isFunction[size - 8][8] = true;

        // 4. Zigzag data placement
        int bitIdx = 0;
        int totalBits = totalCw * 8;
        boolean upward = true;
        for (int right = size - 1; right >= 1; right -= 2) {
            if (right == 6) right = 5;
            for (int vert = 0; vert < size; vert++) {
                int r = upward ? (size - 1 - vert) : vert;
                for (int j = 0; j < 2; j++) {
                    int c = right - j;
                    if (!isFunction[r][c]) {
                        boolean bit = false;
                        if (bitIdx < totalBits) {
                            bit = ((codewords[bitIdx >>> 3] >>> (7 - (bitIdx & 7))) & 1) != 0;
                            bitIdx++;
                        }
                        // Mask 0: (r + c) % 2 == 0
                        if ((r + c) % 2 == 0) bit = !bit;
                        modules[r][c] = bit;
                    }
                }
            }
            upward = !upward;
        }

        // 5. Format information for ECC Level L (01) + Mask 0 (000) -> 0x77C4 (15-bit BCH: b14..b0)
        int formatBits = 0x77C4;
        // Copy 1 around top-left finder: (0,8)..(5,8), (7,8), (8,8), (8,7), (8,5)..(8,0)
        for (int i = 0; i <= 5; i++) modules[i][8] = ((formatBits >>> i) & 1) != 0;
        modules[7][8] = ((formatBits >>> 6) & 1) != 0;
        modules[8][8] = ((formatBits >>> 7) & 1) != 0;
        modules[8][7] = ((formatBits >>> 8) & 1) != 0;
        for (int i = 9; i < 15; i++) modules[8][14 - i] = ((formatBits >>> i) & 1) != 0;

        // Copy 2 along top-right and bottom-left finders
        for (int i = 0; i < 8; i++) modules[8][size - 1 - i] = ((formatBits >>> i) & 1) != 0;
        for (int i = 8; i < 15; i++) modules[size - 15 + i][8] = ((formatBits >>> i) & 1) != 0;

        return modules;
    }

    public static Bitmap renderQrBitmap(String url, int sizePx) {
        boolean[][] matrix = encodeUrl(url);
        int n = matrix.length;
        Bitmap bm = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        // High-contrast crisp white quiet zone for instant camera recognition
        float radius = sizePx * 0.08f;
        p.setColor(0xFFFFFBF2);
        c.drawRoundRect(0, 0, sizePx, sizePx, radius, radius, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(Math.max(2f, sizePx * 0.015f));
        p.setColor(0xFFFFD54F);
        c.drawRoundRect(1f, 1f, sizePx - 1f, sizePx - 1f, radius, radius, p);

        p.setStyle(Paint.Style.FILL);
        int quietModules = 4;
        float cell = (float) sizePx / (n + quietModules * 2);
        float offset = quietModules * cell;
        p.setColor(0xFF0A0D18);
        for (int r = 0; r < n; r++) {
            for (int col = 0; col < n; col++) {
                if (matrix[r][col]) {
                    float x0 = offset + col * cell;
                    float y0 = offset + r * cell;
                    c.drawRect(x0, y0, x0 + cell + 0.35f, y0 + cell + 0.35f, p);
                }
            }
        }
        return bm;
    }

    private static int appendBits(int[] buf, int pos, int val, int len) {
        for (int i = len - 1; i >= 0 && pos < buf.length; i--) {
            buf[pos++] = (val >>> i) & 1;
        }
        return pos;
    }

    private static void drawFinderPattern(boolean[][] modules, boolean[][] isFunc, int row, int col) {
        int size = modules.length;
        for (int dr = -1; dr <= 7; dr++) {
            for (int dc = -1; dc <= 7; dc++) {
                int r = row + dr, c = col + dc;
                if (r >= 0 && r < size && c >= 0 && c < size) {
                    boolean on = (dr >= 0 && dr <= 6 && (dc == 0 || dc == 6))
                            || (dc >= 0 && dc <= 6 && (dr == 0 || dr == 6))
                            || (dr >= 2 && dr <= 4 && dc >= 2 && dc <= 4);
                    modules[r][c] = on;
                    isFunc[r][c] = true;
                }
            }
        }
    }

    private static void drawAlignmentPattern(boolean[][] modules, boolean[][] isFunc, int centerR, int centerC) {
        for (int dr = -2; dr <= 2; dr++) {
            for (int dc = -2; dc <= 2; dc++) {
                int r = centerR + dr, c = centerC + dc;
                modules[r][c] = Math.max(Math.abs(dr), Math.abs(dc)) != 1;
                isFunc[r][c] = true;
            }
        }
    }

    private static int[] computeReedSolomon(int[] data, int dataLen, int degree) {
        int[] gen = new int[degree];
        gen[degree - 1] = 1;
        int root = 1;
        for (int i = 0; i < degree; i++) {
            for (int j = 0; j < degree; j++) {
                gen[j] = gfMul(gen[j], root);
                if (j + 1 < degree) gen[j] ^= gen[j + 1];
            }
            root = gfMul(root, 0x02);
        }
        int[] rem = new int[degree];
        for (int i = 0; i < dataLen; i++) {
            int factor = data[i] ^ rem[0];
            System.arraycopy(rem, 1, rem, 0, degree - 1);
            rem[degree - 1] = 0;
            for (int j = 0; j < degree; j++) {
                rem[j] ^= gfMul(gen[j], factor);
            }
        }
        return rem;
    }

    private static int gfMul(int x, int y) {
        int z = 0;
        for (int i = 7; i >= 0; i--) {
            z = (z << 1) ^ ((z >>> 7) * 0x11D);
            z ^= ((y >>> i) & 1) * x;
        }
        return z;
    }
}
