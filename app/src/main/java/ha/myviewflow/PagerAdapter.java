package ha.myviewflow;

import android.content.Context;
import android.support.v4.util.Pools;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yuexi on 2016/11/9.
 */

public class PagerAdapter extends android.support.v4.view.PagerAdapter {

    private Context context;
    private OnPagerItemClickListnener listener;
    private List<String> urlList;
    private Pools.Pool<ImageView> pool;

    public PagerAdapter(Context context, OnPagerItemClickListnener listener, List<String> urlList) {
        this.context = context;
        this.listener = listener;
        this.urlList = urlList;
        pool = new Pools.SimplePool<>(urlList.size()+1);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position%=urlList.size();
        final int tmpPos=position;
        //图片的显示
        ImageView imageView = pool.acquire();
        if (imageView==null)imageView=new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.with(context)
                .load(urlList.get(position))
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null) listener.onClick(tmpPos);
            }
        });

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        pool.release((ImageView) object);
    }
}
interface OnPagerItemClickListnener{
    void onClick(int pos);
}