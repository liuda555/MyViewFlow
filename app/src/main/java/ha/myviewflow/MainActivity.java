package ha.myviewflow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
/*
   暴漏出去的item点击事件接口：OnPagerItemClickListnener
   需要传递的参数：
        item 个数
        List<url>
        Circle 形状，颜色【包括normal和select】
        轮播时间间隔

        viewpager加载前图片，加载失败图片
 */

public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private LinearLayout layout;

    private List<String> urlList=new ArrayList<>();
    private List<Integer> posList=new ArrayList<>();
    private boolean once=true;
    private TextView circle;
    private PagerAdapter adapter;
    private RelativeLayout main_layout;
    private int preItemPos=0;
    private boolean right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.viewPager);
        layout = (LinearLayout) findViewById(R.id.circleLayout);
        main_layout = (RelativeLayout) findViewById(R.id.activity_main);

        //传入参数为5;传入的url集合为urlList;间隔时间为500ms;传入默认颜色为白，移动颜色为红
        final LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(5,5,1);
        params.setMargins(5,0,5,0);
        for (int i = 0; i < 5; i++) {
            TextView circle=(TextView) LayoutInflater.from(this).inflate(R.layout.circle, null);
            circle.setLayoutParams(params);
            layout.addView(circle);
        }

        circle = (TextView) LayoutInflater.from(this).inflate(R.layout.circle, null);
        RelativeLayout.LayoutParams params1=new RelativeLayout.LayoutParams(5,5);
        circle.setLayoutParams(params1);
        circle.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_rotate));
        main_layout.addView(circle);

        urlList.add("http://img0.imgtn.bdimg.com/it/u=3508535310,2065429756&fm=21&gp=0.jpg");
        urlList.add("http://img5.imgtn.bdimg.com/it/u=646811362,1303793773&fm=21&gp=0.jpg");
        urlList.add("http://imgsrc.baidu.com/baike/pic/item/0d72994497e8a063510ffee5.jpg");
        urlList.add("http://img0.imgtn.bdimg.com/it/u=1342487761,3658569123&fm=21&gp=0.jpg");
        urlList.add("http://img1.imgtn.bdimg.com/it/u=2656478986,1405599153&fm=21&gp=0.jpg");

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        if (once){
                            posList.add(layout.getTop());
                            posList.add(layout.getLeft());
                            once=false;
                        }
                        posList.add(layout.getChildAt(i).getLeft());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new PagerAdapter(MainActivity.this, new tmpListener(), urlList);
                            pager.setCurrentItem(Integer.MAX_VALUE/2-Integer.MAX_VALUE/2%urlList.size());
                            pager.setAdapter(adapter);

                            circle.setTop(posList.get(0));
                            circle.setLeft(posList.get(1)+posList.get(2));
                            myHandler.sendEmptyMessageDelayed(100,1000);
                            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                }

                                @Override
                                public void onPageSelected(int position) {
                                    myHandler.sendEmptyMessageDelayed(100,2000);
                                    right=position>preItemPos?true:false;
                                    preItemPos=position;
                                    position%=urlList.size();
                                    //圆点的移动
                                    AnimationSet animationSet=new AnimationSet(true);
                                    Animation animation;
                                    if (position==0){
                                        if (right)animation=setAnim(posList.get(2)-5,posList.get(2));
                                        else animation=setAnim(posList.get(position+3),posList.get(position+2));
                                    }
                                    else {
                                        if (right)animation=setAnim(posList.get(position+1),posList.get(position+2));
                                        else{
                                            if (position==urlList.size()-1)animation=setAnim(posList.get(posList.size()-1)+5,posList.get(posList.size()-1));
                                            else animation=setAnim(posList.get(position+3),posList.get(position+2));
                                        }
                                    }
                                    animation.setDuration(1000);
                                    animationSet.addAnimation(animation);
                                    animationSet.setFillEnabled(true);
                                    animationSet.setFillAfter(true);
                                    circle.startAnimation(animationSet);
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {
                                    switch (state){
                                        case 1:
                                            myHandler.removeMessages(100);
                                            myHandler.sendEmptyMessageDelayed(100,5000);
                                            Log.e("自定义标签", "类名==MainActivity" + "方法名==onPageScrollStateChanged=====:" + "");
                                            break;
                                    }
                                }
                            });
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 100:
                    myHandler.removeMessages(100);
                    pager.setCurrentItem(pager.getCurrentItem()+1);
                    break;
            }
        }
    };

    private Animation setAnim(int from,int to){
        return new TranslateAnimation(posList.get(1)+from,posList.get(1)+to,posList.get(0),posList.get(0));
    }

}
//暴露出去的接口；在此处实现Viewpager的item的监听
class tmpListener implements OnPagerItemClickListnener{

    @Override
    public void onClick(int pos) {
        Log.e("自定义标签", "类名==tmpListener" + "方法名==onClick=====:" + pos);
    }
}
