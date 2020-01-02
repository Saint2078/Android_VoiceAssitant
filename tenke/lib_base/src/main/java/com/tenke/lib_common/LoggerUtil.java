package com.tenke.lib_common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class LoggerUtil {

    public static void init(final boolean enablePrint, final boolean enableStorage) {
        FormatStrategy prettyFormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .methodOffset(0)
                .logStrategy(new CustomLogCatStrategy())
                .tag("")
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(prettyFormatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return enablePrint;
            }
        });

        Logger.addLogAdapter(new DiskLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return enableStorage;
            }
        });
    }

    public static void init(String tag,final boolean enablePrint, final boolean enableStorage) {
        FormatStrategy prettyFormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .methodOffset(0)
                .logStrategy(new CustomLogCatStrategy())
                .tag(tag)
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(prettyFormatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return enablePrint;
            }

        });

        Logger.addLogAdapter(new DiskLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return enableStorage;
            }
        });
    }

    public static void init(FormatStrategy formatStrategy, final boolean enablePrint, final boolean enableStorage) {

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return enablePrint;
            }

        });

        Logger.addLogAdapter(new DiskLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return enableStorage;
            }
        });
    }


    private static class CustomLogCatStrategy implements LogStrategy {
        @Override
        public void log(int priority, @Nullable String tag, @NonNull String message) {
            Log.println(priority, randomKey() + tag, message);
        }

        private int last;

        private String randomKey() {
            int random = (int) (10 * Math.random());
            if (random == last) {
                random = (random + 1) % 10;
            }
            last = random;
            return String.valueOf(random);

        }
    }
}
