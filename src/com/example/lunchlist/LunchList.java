package com.example.lunchlist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LunchList extends ListActivity {
	
 public final static String ID_EXTRA="com.example._ID";
 Cursor model=null;
 RestaurantHelper helper=null; 
 RestaurantAdapter adapter=null;
 SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
	
 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		
		helper = new RestaurantHelper(this);	
        prefs=PreferenceManager.getDefaultSharedPreferences(this);	
        initList();
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
 
 }
 
 private SharedPreferences.OnSharedPreferenceChangeListener prefListener=new SharedPreferences.OnSharedPreferenceChangeListener() {
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs,
			String key) {
		if(key.equals("sort_order")){
			initList();
		}
		
	}
};

private void initList(){
	if(model!=null){
		stopManagingCursor(model);
		model.close();
	}
	model=helper.getAll(prefs.getString("sort_order", "name"));
	startManagingCursor(model);
	adapter=new RestaurantAdapter(model);
	setListAdapter(adapter);
}
 
 @Override
 public void onDestroy(){
	 super.onDestroy();
	 
	 helper.close();
 }
 
 @Override
 public void onListItemClick (ListView list, View view, int position, long id){
	 Intent i= new Intent(LunchList.this,DetailForm.class);
	 i.putExtra(ID_EXTRA, String.valueOf(id));
	 startActivity(i);
 }
 
 @Override
 public boolean onCreateOptionsMenu(Menu menu){
	 new MenuInflater(this).inflate(R.menu.option, menu);
	 
	 return (super.onCreateOptionsMenu(menu));
 }
 
 @Override
 public boolean onOptionsItemSelected(MenuItem item){
	 if(item.getItemId()==R.id.add){
		 startActivity(new Intent(LunchList.this,DetailForm.class));
	     return (true);	 
	  }
	 else if(item.getItemId()==R.id.prefs){
		 startActivity(new Intent(this,EditPreferences.class));
		 return (true);
	 }
	 return (super.onOptionsItemSelected(item));
	 }

	class RestaurantAdapter extends CursorAdapter{
		RestaurantAdapter(Cursor c){
			super(LunchList.this,c);
		}
		@Override
		public void bindView(View row, Context ctxt, Cursor c) {
			RestaurantHolder holder= (RestaurantHolder)row.getTag();
			
			holder.populateForm(c,helper);
			
		}
		@Override
		public View newView(Context ctxt, Cursor c, ViewGroup parent) {
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.row, parent,false);
			RestaurantHolder holder=new RestaurantHolder(row);
			row.setTag(holder);
			return (row);
		}
	}
 static class RestaurantHolder{
	 private TextView name=null;
	 private TextView address=null;
	 private ImageView icon=null;
	 
	 RestaurantHolder(View row){
		 name=(TextView)row.findViewById(R.id.title);
		 address=(TextView)row.findViewById(R.id.address);
		 icon=(ImageView)row.findViewById(R.id.icon);
	 
	 }
	 void populateForm(Cursor c,RestaurantHelper helper){
		 name.setText(helper.getName(c));
		 address.setText(helper.getAddress(c));
		 
		 
		 if(helper.getType(c).equals("dine_in")){
			 icon.setImageResource(R.drawable.ic_launcher);
		 }
		 else if(helper.getType(c).equals("take_away")){
			icon.setImageResource(R.drawable.ic_launcher); 
		 }
		 else{
			 icon.setImageResource(R.drawable.ic_launcher);
		 }
	 }
 }
	
}
