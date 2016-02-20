package com.rerx.alexey.audiocontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import view.SlidingTabLayout;

/**
 * Created by mihail on 20.02.16.
 */
public class UI {

    Context context;
    MainActivity mainActivity;
    FilesControl filesControl;
    Tablature tab;

    UI(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        filesControl = mainActivity.filesControl;
        tab = mainActivity.tab;
    }

    void preparePager() {

    }

    void setStartAlertDialog() {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        layout.setOrientation(LinearLayout.VERTICAL);

        ViewPager pager = new ViewPager(context);
        pager.setAdapter(new myPager());
        pager.setLayoutParams(new LinearLayout.LayoutParams(-1, 90));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if(position==1){
//                    pager.setLayoutParams(new LinearLayout.LayoutParams(-1,  90+(int)(110*positionOffset)));
//                    Log.i("scroll",""+(90+(int)(110*positionOffset)));
//                }else
//                if(position==2){
//                    (pager).setLayoutParams(new LinearLayout.LayoutParams(-1,  200-(int)(110*positionOffset)));
//                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    (pager).setLayoutParams(new LinearLayout.LayoutParams(-1, 200));
                } else {
                    (pager).setLayoutParams(new LinearLayout.LayoutParams(-1, 90));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        SlidingTabLayout slidingTab = new SlidingTabLayout(context);
        slidingTab.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        slidingTab.setViewPager(pager);

        layout.addView(slidingTab);
        layout.addView(pager);


        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (dialog1, which1) -> dialog1.dismiss())
//                .setOnCancelListener((view) -> {
//                    tab.setBPM(120);
//                    Toast.makeText(context, getString(R.string.choosen_defaul_bpm),
//                            Toast.LENGTH_SHORT)
//                            .show();
//                    startRecord();
//                })
                .setView(layout)
                .setPositiveButton(mainActivity.getString(R.string.ok), (dialog, which) -> {
                    switch (pager.getCurrentItem()) {
                        case 0: { //Создание новой композиции
                            mainActivity.startRecord();
                            break;
                        }
                        case 1: { //Загрузка сохраненного таба
                            try {
                                tab.loadTabulature(filesControl.openTab(
                                                filesControl.getSavedTabsChooser()
                                                        .getSelectedItem()
                                                        .toString())
                                );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case 2: {
                            mainActivity.startRecord();
                            break;
                        }
                    }

                })
                .create()
                .show();
    }

    void setStopAlertDialog() {
        final EditText tabNameEdit = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle("Введите название таба")
                .setCancelable(false)
                .setPositiveButton("Сохранить",
                        (dialog, which) -> {
                            //save
                            boolean result;
                            try {
                                result = filesControl.saveTab(tabNameEdit.getText().toString(), tab);
                            } catch (IOException e) {
                                Toast.makeText(context,
                                        mainActivity.getString(R.string.tab_saving_error),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                e.printStackTrace();
                            }
                            mainActivity.stopRecordBtn.setEnabled(false);
                            dialog.dismiss();
                        })
                .setNegativeButton("Отмена",
                        (dialog, which) -> dialog.dismiss())
                .setView(tabNameEdit)
                .create()
                .show();

        tabNameEdit.setText("Новый таб 1");
    }

    class myPager extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Spinner spinner = new Spinner(context);

            if (position == 0) {
                spinner = tab.getBpmSetter();
                Log.i("instantItem", "[" + position + "] setted");
                container.addView(spinner);
                return spinner;
            } else if (position == 1) {
                spinner = filesControl.getSavedTabsChooser();
                Log.i("instantItem", "[" + position + "] setted");
                container.addView(spinner);
                return spinner;
            } else if (position == 2) {
                LinearLayout layout = setSettingsLayout();
                container.addView(layout);
                return layout;
            }

            return null;

        }


        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence sequence = "";
            if (position == 0) {
                sequence = "Установите темп";
            } else if (position == 1) {
                sequence = "Выберете файл";
            } else if (position == 2) {
                sequence = "Настройки";
            }
            return sequence;
        }

        LinearLayout setSettingsLayout() {
            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

            TextView txt = new TextView(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            txt.setText(mainActivity.getString(R.string.buffer_size));
            txt.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
            txt.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 2));
            layout.addView(txt);

            layout.addView(mainActivity.getBufferSizeChooser());


            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(mainActivity.getString(R.string.visualization_switch));
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                mainActivity.isVisualized = isChecked;
            });
            checkBox.setChecked(mainActivity.isVisualized);
            layout.addView(checkBox);

            Button button = new Button(context);
            button.setText(mainActivity.getString(R.string.calibration_button));
            button.setOnClickListener(v -> new Calibration(context).start());
            button.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));

            layout.addView(button);
            return layout;
        }
    }

}
