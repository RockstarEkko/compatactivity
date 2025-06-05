package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private static final String TAG = "CurrencyParser";
    private List<CurrencyRate> currencyRates = new ArrayList<>();
    private CurrencyAdapter adapter;

    // 货币代码到中文名称的映射
    private static final Map<String, String> CURRENCY_MAP = new HashMap<>();
    static {
        CURRENCY_MAP.put("USD", "美元");
        CURRENCY_MAP.put("EUR", "欧元");
        CURRENCY_MAP.put("JPY", "日元");
        CURRENCY_MAP.put("GBP", "英镑");
        CURRENCY_MAP.put("TWD", "新台币");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        new FetchExchangeRatesTask().execute();

        // 列表点击事件
        listView.setOnItemClickListener((parent, view, position, id) -> {
            CurrencyRate selected = currencyRates.get(position);
            Intent intent = new Intent(MainActivity.this, CalculateActivity.class);
            intent.putExtra("CURRENCY_NAME", selected.getName()); // 新增币种名称
            intent.putExtra("RATE", selected.getRate());
            startActivity(intent);
        });
    }

    // 自定义Adapter
    class CurrencyAdapter extends ArrayAdapter<CurrencyRate> {
        public CurrencyAdapter(Context context, List<CurrencyRate> rates) {
            super(context, 0, rates);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CurrencyRate rate = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item, parent, false);
            }
            TextView name = convertView.findViewById(R.id.currencyName);
            TextView exchange = convertView.findViewById(R.id.exchangeRate);
            name.setText(rate.getName());
            exchange.setText(rate.getRate());
            return convertView;
        }
    }