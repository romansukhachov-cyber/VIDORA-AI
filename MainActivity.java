package com.vidora.ai;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private LinearLayout root, content;
    private EditText prompt;
    private Spinner styleSpinner, durationSpinner;
    private TextView status;
    private final ArrayList<String> videos = new ArrayList<>();

    int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    TextView text(String s, float size) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.WHITE); t.setPadding(dp(16),dp(8),dp(16),dp(8)); return t;
    }
    Button button(String s) {
        Button b = new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(14); b.setAllCaps(false); b.setBackgroundColor(Color.rgb(111,78,220)); return b;
    }
    LinearLayout card() { LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(dp(12),dp(12),dp(12),dp(12)); c.setBackgroundColor(Color.rgb(25,25,36)); return c; }

    @Override public void onCreate(Bundle b) { super.onCreate(b); showHome(); }

    void base(String title) {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(11,11,18));
        TextView bar = text(title, 22); bar.setTypeface(Typeface.DEFAULT, Typeface.BOLD); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setBackgroundColor(Color.rgb(18,18,27)); bar.setPadding(dp(18),dp(14),dp(18),dp(14));
        root.addView(bar, new LinearLayout.LayoutParams(-1, dp(64)));
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(16),dp(16),dp(16),dp(16));
        ScrollView scroll = new ScrollView(this); scroll.addView(content); root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    void showHome() {
        base("VIDORA AI");
        TextView hero=text("Создавай видео с помощью AI",28); hero.setTypeface(Typeface.DEFAULT,Typeface.BOLD); content.addView(hero);
        content.addView(text("Сценарий, сцены, озвучка и субтитры — в одном приложении.",16));
        Space sp=new Space(this); content.addView(sp,new LinearLayout.LayoutParams(1,dp(20)));
        Button create=button("🎬  Создать видео"); content.addView(create,new LinearLayout.LayoutParams(-1,dp(58))); create.setOnClickListener(v->showCreate());
        Space sp2=new Space(this); content.addView(sp2,new LinearLayout.LayoutParams(1,dp(20)));
        LinearLayout c=card(); c.addView(text("Что умеет VIDORA",19)); c.addView(text("• Shorts / TikTok / Reels\n• Формат 9:16\n• Cinematic, Cartoon, Anime\n• AI-озвучка и субтитры\n• История созданных роликов",15)); content.addView(c);
        Button lib=button("Мои видео"); content.addView(lib,new LinearLayout.LayoutParams(-1,dp(52))); lib.setOnClickListener(v->showLibrary());
    }

    void showCreate() {
        base("Создать видео");
        content.addView(text("Опиши, какое видео хочешь получить",18));
        prompt=new EditText(this); prompt.setHint("Например: Улитка Джакуй путешествует по Японии..."); prompt.setHintTextColor(Color.GRAY); prompt.setTextColor(Color.WHITE); prompt.setGravity(Gravity.TOP); prompt.setMinLines(5); prompt.setPadding(dp(12),dp(12),dp(12),dp(12)); prompt.setBackgroundColor(Color.rgb(25,25,36)); content.addView(prompt,new LinearLayout.LayoutParams(-1,dp(150)));
        content.addView(text("Стиль",16)); styleSpinner=new Spinner(this); styleSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Cinematic","Cartoon","Anime","Realistic"})); content.addView(styleSpinner);
        content.addView(text("Длительность",16)); durationSpinner=new Spinner(this); durationSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"15 секунд","30 секунд","60 секунд"})); content.addView(durationSpinner);
        content.addView(text("Формат: 9:16 вертикальный",14));
        Button gen=button("✨  GENERATE VIDEO"); content.addView(gen,new LinearLayout.LayoutParams(-1,dp(58))); gen.setOnClickListener(v->generate());
        Button back=button("Назад"); content.addView(back,new LinearLayout.LayoutParams(-1,dp(48))); back.setOnClickListener(v->showHome());
    }

    void generate() {
        String p=prompt.getText().toString().trim(); if(p.isEmpty()){ prompt.setError("Введите идею видео"); return; }
        base("Генерация"); status=text("Подготовка проекта…\n\nЭто демонстрационная генерация. Настоящий AI будет подключён через защищённый сервер.",17); content.addView(status);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal); bar.setMax(100); content.addView(bar,new LinearLayout.LayoutParams(-1,dp(20)));
        HandlerDelay.run(bar,status,p,()->{ videos.add(p); showLibrary(); });
    }

    void showLibrary() {
        base("Мои видео");
        if(videos.isEmpty()) content.addView(text("Пока нет созданных видео.\nНажми «Создать видео», чтобы сделать первый проект.",17));
        for(String v:videos){ LinearLayout c=card(); c.addView(text("🎬  "+v,17)); c.addView(text("9:16 • AI project",13)); content.addView(c); }
        Button create=button("Создать новое видео"); content.addView(create,new LinearLayout.LayoutParams(-1,dp(54))); create.setOnClickListener(x->showCreate());
        Button home=button("Главная"); content.addView(home,new LinearLayout.LayoutParams(-1,dp(48))); home.setOnClickListener(x->showHome());
    }

    static class HandlerDelay {
        static void run(ProgressBar bar, TextView status, String p, Runnable done) {
            final int[] x={0}; android.os.Handler h=new android.os.Handler(); Runnable r=new Runnable(){ public void run(){ x[0]+=10; bar.setProgress(x[0]); String[] s={"Анализ идеи…","Создание сценария…","Подготовка сцен…","Подготовка озвучки…","Сборка видео…","Готово!"}; status.setText(s[Math.min(5,x[0]/20)]); if(x[0]<100) h.postDelayed(this,350); else h.postDelayed(done,400); }}; h.post(r);
        }
    }
}
