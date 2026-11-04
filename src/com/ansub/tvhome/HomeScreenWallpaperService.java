package com.ansub.tvhome;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * System-Wide Android Lock Screen & Home Screen 120 FPS Procedural Live Wallpaper Engine.
 * Renders the user's chosen Theme Universe (all 9 Universes & 432 Story Scenes), Living Weather,
 * and Touch-Ripple Physics directly on the Android Lock Screen and any Wallpaper host.
 */
public class HomeScreenWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new StoryWallpaperEngine();
    }

    private class StoryWallpaperEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private SkyView skyView;
        private boolean visible = false;
        private int surfaceW = 1080;
        private int surfaceH = 2400;

        private final Runnable drawFrameTask = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
            initSkyView();
        }

        private void initSkyView() {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            float sw = dm.widthPixels / 1920f;
            float sh = dm.heightPixels / 1080f;
            float scale = (dm.heightPixels > dm.widthPixels)
                    ? Math.max(0.68f, Math.max(sw * 1.32f, dm.density * 0.32f))
                    : Math.max(0.42f, Math.min(sw, sh * 1.18f));
            Typeface cinzel;
            try {
                cinzel = Typeface.createFromAsset(getAssets(), "cinzel.ttf");
            } catch (Throwable t) {
                cinzel = Typeface.create("sans-serif-medium", Typeface.NORMAL);
            }
            skyView = new SkyView(HomeScreenWallpaperService.this, scale, 0, cinzel);
            syncPrefs();
        }

        private void syncPrefs() {
            if (skyView == null) return;
            SharedPreferences p = getSharedPreferences("config", 0);
            int th = Math.max(0, Math.min(12, p.getInt("universe_theme", 0)));
            skyView.setUniverseTheme(th);
            skyView.setWeatherMood(p.getInt("weather_mood", 0));
            skyView.setStorySpeedMultiplier(p.getFloat("story_speed", 1.0f));
            skyView.setLiveWeatherCondition(p.getString("weather_text", "66\u00B0F Clear"));
            if (p.getBoolean("story_locked", false)) {
                skyView.jumpToScene(p.getInt("story_locked_idx", 0), true);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                syncPrefs();
                handler.removeCallbacks(drawFrameTask);
                handler.post(drawFrameTask);
            } else {
                handler.removeCallbacks(drawFrameTask);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            this.surfaceW = Math.max(1, width);
            this.surfaceH = Math.max(1, height);
            if (skyView != null) {
                int wSpec = View.MeasureSpec.makeMeasureSpec(surfaceW, View.MeasureSpec.EXACTLY);
                int hSpec = View.MeasureSpec.makeMeasureSpec(surfaceH, View.MeasureSpec.EXACTLY);
                skyView.measure(wSpec, hSpec);
                skyView.layout(0, 0, surfaceW, surfaceH);
            }
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(drawFrameTask);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if (skyView != null && event != null) {
                int act = event.getActionMasked();
                if (act == MotionEvent.ACTION_DOWN || act == MotionEvent.ACTION_MOVE) {
                    skyView.onUserTouch(event.getX(), event.getY(), act == MotionEvent.ACTION_DOWN);
                }
            }
        }

        private void drawFrame() {
            handler.removeCallbacks(drawFrameTask);
            if (!visible || skyView == null) return;
            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    skyView.draw(c);
                }
            } catch (Throwable ignored) {
            } finally {
                if (c != null) {
                    try { holder.unlockCanvasAndPost(c); } catch (Throwable ignored) {}
                }
            }
            if (visible) {
                handler.postDelayed(drawFrameTask, 16L);
            }
        }
    }
}

