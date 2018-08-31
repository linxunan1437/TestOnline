package GUISetting;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by lh on 2018/8/16.
 */

public class myFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    myFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        //该方法在滑到已经缓存的页面时，并不被调用。缓存外已经创建过并被销毁的页面，还会再调用该方法，重新创建。
        //Log.i(TAG, NAME + "--getItem++position:" + position);
        return mFragments.get(position);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //除非碰到 FragmentManager 刚好从 SavedState 中恢复了对应的 Fragment 的情况外(从页面缓存中恢复)，该函数将会调用 getItem() 函数，生成新的 Fragment 对象。新的对象将被 FragmentTransaction.add()。
        //Log.i(TAG, NAME + "--instantiateItem++container:" + container.getChildCount() + "++position:" + position);
        return super.instantiateItem(container, position);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //超出缓存的页面，将调用该方法从视图中移除
        //Log.i(TAG, NAME + "--destroyItem++container:" + container.getChildCount() + "++position:" + position);
        super.destroyItem(container, position, object);
    }
    @Override
    public int getCount() {
        //Log.i(TAG, NAME + "--getCount++");
        return mFragments.size();
    }
}
