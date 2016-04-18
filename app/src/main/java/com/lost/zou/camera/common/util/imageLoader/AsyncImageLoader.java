package com.lost.zou.camera.common.util.imageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;

import com.lost.zou.camera.common.Constant;
import com.lost.zou.camera.common.util.FileUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.LoadLocalImageListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;

/**
 * Created by Administrator on 2015/8/11.
 * <br>类描述:异步加载图片
 * <br>功能详细描述:异步加载图片
 */
public class AsyncImageLoader {
    /**
     * image path format
     * "http://site.com/image.png" // from Web
     * "file:///mnt/sdcard/image.png" // from SD card
     * "file:///mnt/sdcard/video.mp4" // from SD card (video thumbnail)
     * "content://media/external/images/media/13" // from content provider
     * "content://media/external/video/media/13" // from content provider (video thumbnail)
     * "assets://image.png" // from assets
     * "drawable://" + R.drawable.img // from drawables (non-9patch images)
     */
    private static class SingletonHolder {
        public final static AsyncImageLoader INSTANCE = new AsyncImageLoader();
    }

    public static AsyncImageLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ImageLoader mImageLoader;

    private int maxImageWidthForMemoryCache;
    private int maxImageHeightForMemoryCache;

    /**
     * <br>功能简述:必须在应用的application 或activity先调用这个方法进行初始化，
     * 才能使用异步加载图片
     * <br>功能详细描述:
     * <br>注意:
     */
    public void init(Context context, int defaultImageId, int cacheSize, String cachePath) {
        DisplayImageOptions options = new Builder()
                .showImageOnLoading(defaultImageId) // resource or drawable
                .showImageForEmptyUri(defaultImageId) // resource or drawable
                .showImageOnFail(defaultImageId) // resource or drawable
                        //.resetViewBeforeLoading(true)  // default
                .cacheInMemory(true).cacheOnDisk(true)

                        //  .considerExifParams(false) // default
                        // .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .build();

        if (TextUtils.isEmpty(cachePath)) {
            cachePath = Constant.Path.sAPP_DIR + "/imageCache";
        }
        File cacheFile = new File(cachePath);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context.getApplicationContext()).threadPriority(Thread.NORM_PRIORITY)
                .threadPoolSize(3)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .diskCache(new UnlimitedDiskCache(cacheFile)).memoryCacheSize(cacheSize)
                .tasksProcessingOrder(QueueProcessingType.LIFO).defaultDisplayImageOptions(options)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        maxImageWidthForMemoryCache = displayMetrics.widthPixels;
        maxImageHeightForMemoryCache = displayMetrics.heightPixels;
    }

    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param url
     * @param imageView
     */
    public void loadImage(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(url, imageView);
    }

    public void loadRoundedImage(String url, ImageView imageView, int radius) {
        RoundedBitmapDisplayer roundedBitmapDisplayer = new RoundedBitmapDisplayer(radius);
        DisplayImageOptions options = new Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(roundedBitmapDisplayer)
                .build();
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }


    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param url
     * @param imageView
     */
    public void loadImage(String url, final ImageView imageView, ImageSize imageSize) {
        ImageLoader.getInstance().loadImage(url, imageSize, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                imageView.setImageBitmap(arg2);

            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void loadImage(String url, ImageView imageView, LoadLocalImageListener listener) {
        ImageLoader.getInstance().displayImage(url, imageView, listener);
    }

    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param imagePath from SD card : /mnt/sdcard/image.png"
     * @param imageView
     */
    public void loadSDImage(String imagePath, ImageView imageView) {
        String imageURL = "file://" + imagePath;
        loadImage(imageURL, imageView);
    }

    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param imagePath from SD card : /mnt/sdcard/image.png"
     * @param imageView
     */
    public void loadSDImageNoDefaul(String imagePath, ImageView imageView) {
        DisplayImageOptions options = new Builder()
                //.resetViewBeforeLoading(true)  // default
                .cacheInMemory(true).cacheOnDisk(true)

                        //  .considerExifParams(false) // default
                        // .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .build();
        String imageURL = "file://" + imagePath;
        mImageLoader.displayImage(imageURL, imageView, options);
    }

    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param imagePath from SD card : /mnt/sdcard/image.png"
     */
    public void loadSDImage(String imagePath, final AsyncImageLoaderListener listener) {
        String imageURL = "file://" + imagePath;
        loadImage(imageURL, listener);
    }

    public void loadRoundedImage(String url, ImageView imageView, int radius, final AsyncImageLoaderListener listener) {
        RoundedBitmapDisplayer roundedBitmapDisplayer = new RoundedBitmapDisplayer(radius);
        DisplayImageOptions options = new Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(roundedBitmapDisplayer)
                .build();
        mImageLoader.displayImage(url, imageView, options, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                listener.onLoadingStarted(arg0, arg1);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                listener.onLoadingFailed(arg0, arg1);
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                listener.onLoadingComplete(arg0, arg1, arg2);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                listener.onLoadingCancelled(arg0, arg1);
            }
        });
    }

//	public void loadRoundedImage(String url, final AsyncImageLoaderListener listener) {
//		RoundedBitmapDisplayer roundedBitmapDisplayer = new RoundedBitmapDisplayer(10);
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//		.cacheInMemory(true)
//		.cacheOnDisk(true)
//		.displayer(roundedBitmapDisplayer)
//		.build();
//		mImageLoader.loadImage(url, options,  new ImageLoadingListener() {
//
//			@Override
//			public void onLoadingStarted(String arg0, View arg1) {
//				listener.onLoadingStarted(arg0, arg1);
//			}
//
//			@Override
//			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//				listener.onLoadingFailed(arg0, arg1);
//			}
//
//			@Override
//			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//				listener.onLoadingComplete(arg0, arg1, arg2);
//			}
//
//			@Override
//			public void onLoadingCancelled(String arg0, View arg1) {
//				listener.onLoadingCancelled(arg0, arg1);
//			}
//		});
//	}

    public void loadImage(String url, ImageView imageView, final AsyncImageLoaderListener listener) {
        mImageLoader.displayImage(url, imageView, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                listener.onLoadingStarted(arg0, arg1);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                listener.onLoadingFailed(arg0, arg1);
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                listener.onLoadingComplete(arg0, arg1, arg2);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                listener.onLoadingCancelled(arg0, arg1);
            }
        });
    }

    public void loadImage(String url, final AsyncImageLoaderListener listener) {
        mImageLoader.loadImage(url, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                listener.onLoadingStarted(arg0, arg1);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                listener.onLoadingFailed(arg0, arg1);
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                listener.onLoadingComplete(arg0, arg1, arg2);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                listener.onLoadingCancelled(arg0, arg1);
            }
        });
    }

    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param url
     * @param width
     * @param height
     * @param listener
     */
    public void loadImage(String url, int width, int height, final AsyncImageLoaderListener listener) {
        ImageSize imageSize = new ImageSize(width, height);
        ImageLoader.getInstance().loadImage(url, imageSize, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                listener.onLoadingStarted(arg0, arg1);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                listener.onLoadingFailed(arg0, arg1);
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                listener.onLoadingComplete(arg0, arg1, arg2);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                listener.onLoadingCancelled(arg0, arg1);
            }
        });
    }

    /**
     * 功能简述:从SD卡加载图片的方法 功能详细描述: 注意:
     *
     * @param imgPath 图片所在路径
     * @param imgName 图片名称
     * @param imgUrl  图片URL
     * @param isCache 是否添加到内存的缓存里面
     * @return
     */
    public Bitmap loadImgFromSD(String imgPath, String imgName,
                                String imgUrl, boolean isCache) {
        Bitmap result = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            if (FileUtil.isSDCardAvaiable()) {
                File file = new File(imgPath + imgName);
                if (file.exists()) {
//					if (width > 0 && height > 0) {
//						options.inJustDecodeBounds = true;
//						BitmapFactory.decodeFile(imgPath + imgName, options);
//						final float scaleH = (options.outHeight + 0.1f) / height;
//						final float scaleW = (options.outWidth + 0.1f) / width;
//						options.inSampleSize = scaleH >= scaleW ? (int) scaleH : (int) scaleW;
//					}
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inJustDecodeBounds = false;
                    result = BitmapFactory.decodeFile(imgPath + imgName);
//					if (result != null && isCache) {
//
//					}
                }
            }
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意: 原图下载，不做缩放
     *
     * @param url
     * @param listener
     */
    public void loadImage(String url, ImageLoadingListener listener) {
        DisplayImageOptions options = new Builder().imageScaleType(
                ImageScaleType.NONE).build();
        mImageLoader.loadImage(url, options, listener);
    }

    /**
     * @param *imageLoader  {@linkplain ImageLoader} instance for controlling
     * @param pauseOnScroll Whether {@linkplain ImageLoader#pause() pause ImageLoader} during touch scrolling
     * @param pauseOnFling  Whether {@linkplain ImageLoader#pause() pause ImageLoader} during fling
     */
    public OnScrollListener setOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        PauseOnScrollListener pauseOnScrollListener = new PauseOnScrollListener(mImageLoader,
                pauseOnScroll, pauseOnFling);
        return pauseOnScrollListener;
    }

    /**
     * @param *imageLoader   {@linkplain ImageLoader} instance for controlling
     * @param pauseOnScroll  Whether {@linkplain ImageLoader#pause() pause ImageLoader} during touch scrolling
     * @param pauseOnFling   Whether {@linkplain ImageLoader#pause() pause ImageLoader} during fling
     * @param customListener Your custom {@link OnScrollListener} for {@linkplain *AbsListView list view} which also
     *                       will be get scroll events
     */
    public OnScrollListener setOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling,
                                                OnScrollListener customListener) {
        PauseOnScrollListener pauseOnScrollListener = new PauseOnScrollListener(mImageLoader,
                pauseOnScroll, pauseOnFling, customListener);
        return pauseOnScrollListener;
    }


    /**
     * <br>功能简述: 图片加载暂停
     * <br>功能详细描述:
     * <br>注意: 配合resume使用
     */
    public void pause() {
        mImageLoader.pause();
    }

    /**
     * <br>功能简述: 图片加载继续
     * <br>功能详细描述:
     * <br>注意:
     */
    public void resume() {
        mImageLoader.resume();
    }

    /**
     * <br>功能简述: 同步加载sd卡图片
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param url
     * @return
     */
    public Bitmap loadSDImageSync(String url) {
        String imageURL = "file://" + url;
        return mImageLoader.loadImageSync(imageURL);
    }

    @SuppressWarnings("deprecation")
    public Bitmap loadImageFromMemory(String url, int width, int height) {
        ImageSize targetSize = new ImageSize(width, height);
        String memoryCacheKey = MemoryCacheUtils.generateKey(url, targetSize);
        MemoryCache memory = mImageLoader.getMemoryCache();
        if (memory != null) {
            return memory.get(memoryCacheKey);
        } else {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public void recycle(String url, int width, int height) {
        ImageSize targetSize = new ImageSize(width, height);
        String memoryCacheKey = MemoryCacheUtils.generateKey(url, targetSize);
        MemoryCache memory = mImageLoader.getMemoryCache();
        if (memory != null) {
            memory.remove(memoryCacheKey);
        }
    }

    /**
     * <br>功能简述:回收内存
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param url
     */
    public void recycle(String url) {
        recycle(url, maxImageWidthForMemoryCache, maxImageHeightForMemoryCache);
    }


}
