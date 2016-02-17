package gr.plushost.prototypeapp.adapters.listviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.items.CategoryItem;

/**
 * Created by billiout on 21/2/2015.
 */
public class CategoriesListAdapter extends BaseAdapter {

    List<CategoryItem> list;
    Context context;

    public CategoriesListAdapter(Context context, List<CategoryItem> list, int parentID){
        this.context = context;
        this.list = new ArrayList<>();

        this.list = getListByParentID(list, parentID);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.listrow_categories, parent, false);
        }

        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(list.get(position).getTitle());

        return v;
    }

    public List<CategoryItem> getList(){
        return list;
    }

    public static List<CategoryItem> getListByParentID(List<CategoryItem> list, int parentID){
        List<CategoryItem> tempList = new ArrayList<>();
        for(CategoryItem item : list){
            if(item.getParent_id() == parentID){
                tempList.add(item);
            }
        }
        return tempList;
    }
}
