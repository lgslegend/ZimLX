package org.zimmob.zimlx.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import org.zimmob.zimlx.R;
import org.zimmob.zimlx.interfaces.FastItem;
import org.zimmob.zimlx.manager.Setup;
import org.zimmob.zimlx.model.App;
import org.zimmob.zimlx.model.DrawerAppItem;
import org.zimmob.zimlx.util.Tool;

import java.util.ArrayList;
import java.util.List;

public class AppDrawerVertical extends CardView {

    public static int itemWidth;
    public static int itemHeightPadding;

    public RecyclerView recyclerView;
    public GridAppDrawerAdapter gridDrawerAdapter;
    public DragScrollBar scrollBar;

    private static List<App> apps;
    private GridLayoutManager layoutManager;
    private RelativeLayout rl;

    public AppDrawerVertical(Context context) {
        super(context);
        preInit();
    }

    public AppDrawerVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit();
    }

    public AppDrawerVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit();
    }

    public void preInit() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                rl = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_app_drawer_vertical_inner, AppDrawerVertical.this, false);
                recyclerView = rl.findViewById(R.id.vDrawerRV);
                layoutManager = new GridLayoutManager(getContext(), Setup.appSettings().getDrawerColumnCount());

                itemWidth = (getWidth() - recyclerView.getPaddingRight() - recyclerView.getPaddingRight()) / layoutManager.getSpanCount();
                init();

                if (!Setup.appSettings().isDrawerShowIndicator())
                    scrollBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (apps == null || layoutManager == null) {
            super.onConfigurationChanged(newConfig);
            return;
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscapeValue();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitValue();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void setPortraitValue() {
        layoutManager.setSpanCount(Setup.appSettings().getDrawerColumnCount());
        gridDrawerAdapter.notifyAdapterDataSetChanged();
    }

    private void setLandscapeValue() {
        layoutManager.setSpanCount(Setup.appSettings().getDrawerRowCount());
        gridDrawerAdapter.notifyAdapterDataSetChanged();
    }

    private void init() {

        itemHeightPadding = Tool.dp2px(5, getContext());
        scrollBar = rl.findViewById(R.id.dragScrollBar);
        scrollBar.setIndicator(new AlphabetIndicator(getContext()), true);
        scrollBar.setClipToPadding(true);
        scrollBar.setDraggableFromAnywhere(true);

        scrollBar.post(() -> scrollBar.setHandleColour(Setup.appSettings().getDrawerFastScrollColor()));

        boolean mPortrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        gridDrawerAdapter = new GridAppDrawerAdapter();
        recyclerView.setAdapter(gridDrawerAdapter);

        if (mPortrait) {
            setPortraitValue();
        } else {
            setLandscapeValue();
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setDrawingCacheEnabled(true);
        List<App> allApps = Setup.appLoader().getAllApps(getContext(), false);
        if (allApps.size() != 0) {
            apps = allApps;
            ArrayList<FastItem.AppItem> items = new ArrayList<>();
            for (int i = 0; i < apps.size(); i++) {
                items.add(new DrawerAppItem(apps.get(i)));
            }
            gridDrawerAdapter.set(items);
        }
        Setup.appLoader().addUpdateListener(apps -> {
            AppDrawerVertical.apps = apps;
            ArrayList<FastItem.AppItem> items = new ArrayList<>();
            for (int i = 0; i < apps.size(); i++) {
                items.add(new DrawerAppItem(apps.get(i)));
            }
            gridDrawerAdapter.set(items);

            return false;
        });

        addView(rl);
    }

    public static class GridAppDrawerAdapter extends FastItemAdapter<FastItem.AppItem> implements INameableAdapter {

        GridAppDrawerAdapter() {
            getItemFilter().withFilterPredicate((IItemAdapter.Predicate<FastItem.AppItem>) (item, constraint) -> !item.getApp().getLabel().toLowerCase().contains(constraint.toString().toLowerCase()));
        }

        @Override
        public Character getCharacterForElement(int element) {
            if (apps != null && element < apps.size() && apps.get(element) != null && apps.get(element).getLabel().length() > 0)
                return apps.get(element).getLabel().charAt(0);
            else return '#';
        }
    }
}