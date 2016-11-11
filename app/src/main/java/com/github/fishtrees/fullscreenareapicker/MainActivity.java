package com.github.fishtrees.fullscreenareapicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MyAdapter adapter;
    private List<AreaNode> dataList = new ArrayList<>();

    private AreaNode dataRoot;
    private AreaNode currentDataNode;
    private Deque<AreaNode> currentSelectedNodes = new ArrayDeque<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildMockData();
        dataList.addAll(dataRoot.children);
        currentDataNode = dataRoot;

        ListView listView = (ListView) findViewById(R.id.addrmgr_activity_area_picker_area_list);
        adapter = new MyAdapter(this, dataList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                AreaNode node = dataList.get(position);
                if (!node.equals(currentSelectedNodes.peek()))
                {
                    currentSelectedNodes.push(node);
                    if (node.isLeaf())
                    {
                        StringBuilder builder = new StringBuilder();
                        while (!currentSelectedNodes.isEmpty())
                        {
                            builder.append(currentSelectedNodes.removeLast().name);
                            builder.append(" - ");
                        }
                        Toast.makeText(MainActivity.this, builder, Toast.LENGTH_SHORT).show();

                        currentDataNode = dataRoot;
                        reloadAreaListView();
                    }
                }
                if (!node.isLeaf())
                {
                    currentDataNode = node;
                    reloadAreaListView();
                }
            }
        });


    }

    private void reloadAreaListView() {
        dataList.clear();
        dataList.addAll(currentDataNode.children);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");

        if (!currentSelectedNodes.isEmpty())
        {
            currentSelectedNodes.pop();
        }

        if (currentDataNode != null && !currentDataNode.isRoot())
        {
            dataList.clear();
            dataList.addAll(currentDataNode.parent.children);
            currentDataNode = currentDataNode.parent;

            adapter.notifyDataSetChanged();
        }
        else
        {
            super.onBackPressed();
        }
    }

    private Collection<String> buildMockAreaNodeNameList(List<AreaNode> nodes)
    {
        ArrayList<String> list = new ArrayList<>(nodes.size());
        for (AreaNode node : nodes)
        {
            list.add(node.name);
        }
        return list;
    }

    private void buildMockData()
    {
        dataRoot = new AreaNode("中国", null);
        AreaNode lv1n1 = new AreaNode("重庆市", dataRoot);
        AreaNode lv1n1lv2n1 = new AreaNode("渝中区", lv1n1);
        AreaNode lv1n1lv2n2 = new AreaNode("江北区", lv1n1);

        AreaNode lv1n2 = new AreaNode("四川省", dataRoot);
        AreaNode lv1n2lv2n1 = new AreaNode("成都市", lv1n2);
        AreaNode lv1n2lv2n1lv3n1 = new AreaNode("武侯区", lv1n2lv2n1);
        AreaNode lv1n2lv2n2 = new AreaNode("宜宾市", lv1n2);
        AreaNode lv1n2lv2n2lv3n1 = new AreaNode("兴文县", lv1n2lv2n2);

    }

    static class AreaNode {
        String name;
        List<AreaNode> children = new ArrayList<>();
        AreaNode parent;

        AreaNode(String name, AreaNode parent) {
            this.name = name;
            this.parent = parent;
            if (null != parent)
            {
                parent.children.add(this);
            }
        }

        boolean isRoot()
        {
            return parent == null;
        }

        boolean isLeaf()
        {
            return children == null || children.isEmpty();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class MyAdapter extends BaseAdapter
    {
        List<AreaNode> nodes;
        LayoutInflater mInflater;
        MyAdapter(Context context, List<AreaNode> nodes) {
            this.mInflater = LayoutInflater.from(context);
            this.nodes = nodes;
        }

        @Override
        public int getCount() {
            return this.nodes.size();
        }

        @Override
        public Object getItem(int i) {
            return nodes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null)
            {
                view = mInflater.inflate(R.layout.area_list_item, parent, false);
            }
            else
            {
                view = convertView;
            }
            TextView nameTextView = (TextView) view.findViewById(R.id.addrmgr_activity_area_picker_area_name);
            nameTextView.setText(nodes.get(position).name);

            return view;
        }
    }
}
