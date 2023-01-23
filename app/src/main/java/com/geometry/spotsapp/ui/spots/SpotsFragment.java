package com.geometry.spotsapp.ui.spots;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.geometry.spotsapp.control.CategoriesManipulator;
import com.geometry.spotsapp.control.CategoriesManipulatorImpl;
import com.geometry.spotsapp.control.CategoryExiststAlreadyExcpetion;
import com.geometry.spotsapp.model.MapInstance;
import com.geometry.spotsapp.R;
import com.geometry.spotsapp.databinding.FragmentSpotsBinding;
import com.geometry.spotsapp.model.DatastorageAccessFactory;
import com.geometry.spotsapp.model.DatastorageListAccess;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SpotsFragment extends Fragment {

    private FragmentSpotsBinding binding;

    //Model
    private DatastorageListAccess datastorage;
    private CategoriesManipulator categoriesManipulatorImpl;
    private DatastorageAccessFactory dsfactory;


    //view
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private HashMap<String, List<String>> expandableListDetail;
    private List<String> expandableListTitle;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        defaultColor = ContextCompat.getColor(getContext(), R.color.catStandardColor);
        expandableListDetail = new HashMap<>();
        dsfactory = new DatastorageAccessFactory();
        categoriesManipulatorImpl = CategoriesManipulatorImpl.getInstance();

        binding = FragmentSpotsBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        datastorage = dsfactory.newDatastorageListAcces(MapInstance.getInstance());

        //ListView
        expandableListView = view.findViewById(R.id.categoryTree);

        this.generateExpandableListFromModel();


        FloatingActionButton button = view.findViewById(R.id.addCategoryButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCatDialog();
            }
        });

        /**
        Button delButton = (Button) view.findViewById(R.id.delete_cat);
        //where button is the button on listView, and view is the view of your item on list

        delButton.setFocusable(false);   /// THIS IS THE SOLUTION

        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //button functionalty   ...
            }
        }); **/

        return view;
    }

    private AlertDialog addCategory = null;
    private void addOverlays(){

    }

    private void addCatDialog() {
        if (addCategory != null)
            addCategory.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = View.inflate(getContext(), R.layout.add_cat_dialog, null);
        initColorPicker(view);
        builder.setView(view);


        final EditText title = view.findViewById(R.id.category_name);

        view.findViewById(R.id.category_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory.dismiss();
            }
        });

        view.findViewById(R.id.category_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = title.getText().toString();
                try {
                    categoriesManipulatorImpl.addCategory(name,defaultColor);
                    addCategory.dismiss();
                    generateExpandableListFromModel();
                } catch (CategoryExiststAlreadyExcpetion e) {
                    Toast.makeText(getContext(),"Category with the name" + name + " exists already", Toast.LENGTH_SHORT).show();
                } catch (IllegalArgumentException e){
                    Toast.makeText(getContext(), "A Category Name should be no more than 16 characters and not less than 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addCategory = builder.show();
    }

    private void generateExpandableListFromModel() {
        List<String> categories = datastorage.getAllCategoryNames();
        categories.stream().forEach((categorie) -> {
            List<String> spots = datastorage.getAllSpotNamesOfCategory(categorie);
            expandableListDetail.put(categorie, spots);
        });
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
        this.initExpandableListView();
    }

    /**
     * Initialise the Expandable-List
     */
    private void initExpandableListView() {
        expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void refreshCategoryView() {

    }

    private class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private HashMap<String, List<String>> expandableListDetail;

        public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                           HashMap<String, List<String>> expandableListDetail) {
            this.context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .get(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String expandedListText = (String) getChild(listPosition, expandedListPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }
            TextView expandedListTextView = (TextView) convertView
                    .findViewById(R.id.expandedListItem);
            expandedListTextView.setText(expandedListText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.expandableListTitle.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableListTitle.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }
            int color = datastorage.getCatColor(listTitle);
            TextView listTitleTextView = (TextView) convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);
            listTitleTextView.setTextSize(20);
            listTitleTextView.setTextColor(color);

            FloatingActionButton button = (FloatingActionButton) convertView.findViewById(R.id.delete_cat);
            //where button is the button on listView, and view is the view of your item on list
            button.setFocusable(false);   /// THIS IS THE SOLUTION

            button.setBackgroundTintList(ColorStateList.valueOf(color));
            button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    removeCatDialog(v, listTitle);
                }
            });

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }
    private AlertDialog deleteCat = null;
    private void removeCatDialog(View v, String name) {
            if (deleteCat != null)
                deleteCat.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            View view = View.inflate(v.getContext(), R.layout.rem_cat_dialog, null);
            builder.setView(view);


            view.findViewById(R.id.rem_cat_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCat.dismiss();
                }
            });

            view.findViewById(R.id.rem_cat_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datastorage.removeCategory(name);
                    deleteCat.dismiss();
                    expandableListTitle.clear();
                    expandableListDetail.clear();
                    generateExpandableListFromModel();
                    expandableListView.invalidateViews();
                }
            });
        deleteCat = builder.show();
    }

    private RelativeLayout relativeLayout;
    private Button pickColorButton;
    private int defaultColor;

    private void initColorPicker(View view){
        pickColorButton = view.findViewById(R.id.pick_color_button);
        relativeLayout = view.findViewById(R.id.add_cat_dialog);

        defaultColor = ContextCompat.getColor(view.getContext(), R.color.catStandardColor);

        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });

    }

    private void openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(getContext(), defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                pickColorButton.setBackgroundColor(defaultColor);
            }
        });
        ambilWarnaDialog.show();
    }
}