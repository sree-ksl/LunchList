package com.example.lunchlist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DetailForm extends Activity{

	EditText name=null;
	EditText address=null;
	EditText notes=null;
	EditText feed=null;
	RadioGroup types=null;
	RestaurantHelper helper=null;
	String restaurantId=null;
	TextView location=null;
	LocationManager locMgr=null;
	
	double latitude=0.0d;
	double longitude=0.0d;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_form);
		
        helper = new RestaurantHelper(this);
		
		name=(EditText)findViewById(R.id.editName);
		address=(EditText)findViewById(R.id.editAddress);
		notes=(EditText)findViewById(R.id.editNotes);
		types=(RadioGroup)findViewById(R.id.types);
		feed=(EditText)findViewById(R.id.feed);
		location=(TextView)findViewById(R.id.location);
		
		locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
		
		
		
		restaurantId=getIntent().getStringExtra(LunchList.ID_EXTRA);
		
		if(restaurantId!=null){
			load();
		}
	}
	
	@Override
	public void onPause(){
		save();
		locMgr.removeUpdates(onLocationChange);
		super.onPause();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		helper.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		new MenuInflater(this).inflate(R.menu.details_option, menu);
		return (super.onCreateOptionsMenu(menu));
		
	}
	
	public boolean onPrepareOptionMenu(Menu menu){
		if(restaurantId==null){
			menu.findItem(R.id.location).setEnabled(false);
			menu.findItem(R.id.map).setEnabled(false);
		}
		return (super.onPrepareOptionsMenu(menu));
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId()==R.id.feed){
			if(isNetworkAvailable()){
				Intent i= new Intent(this,FeedActivity.class);
				
				i.putExtra(FeedActivity.FEED_URL, feed.getText().toString());
				startActivity(i);
			}
			else {
				Toast.makeText(this,"Sorry,the Internet is not available", Toast.LENGTH_LONG).show();
			}
			return (true);
		}
		else if(item.getItemId()==R.id.location){
			locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
			return (true);
		}
		else if(item.getItemId()==R.id.map){
			Intent i=new Intent(this,RestaurantMap.class);
			
			i.putExtra(RestaurantMap.EXTRA_LATITUDE, latitude);
			i.putExtra(RestaurantMap.EXTRA_LONGITUDE, longitude);
			i.putExtra(RestaurantMap.EXTRA_NAME, name.getText().toString());
			
			startActivity(i);
			return (true);
		}
		return (super.onOptionsItemSelected(item));
		
	}
	
	private boolean isNetworkAvailable(){
		ConnectivityManager cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info=cm.getActiveNetworkInfo();
		
		return (info!=null);
	}
	
	public void load(){
		Cursor c=helper.getById(restaurantId);
		
		c.moveToFirst();
		name.setText(helper.getName(c));
		address.setText(helper.getAddress(c));
		notes.setText(helper.getNotes(c));
		feed.setText(helper.getFeed(c));
		
		if(helper.getType(c).equals("sit_down")){
			types.check(R.id.dine_in);
		}
		else if(helper.getType(c).equals("take_out")){
			types.check(R.id.take_out);
		}
		else {
			types.check(R.id.delivery);
		}
		latitude=helper.getLatitude(c);
		longitude=helper.getLongitude(c);
		location.setText(String.valueOf(helper.getLatitude(c))+","+String.valueOf(helper.getLongitude(c)));
		c.close();
	}
	
private void save() {
              String type=null;
              
              switch (types.getCheckedRadioButtonId()){
              case R.id.dine_in:
            	  type="dine_in";
            	  break;
              case R.id.take_out:
            	  type="take_away";
            	  break;
              case R.id.delivery:
            	  type="delivery";
            	  break;
			}
              if(restaurantId==null){
            	  helper.insert(name.getText().toString(), address.getText().toString(), type, notes.getText().toString(), feed.getText().toString());
              }
              else {
            	  helper.update(restaurantId,name.getText().toString(),address.getText().toString(), type, notes.getText().toString(), feed.getText().toString());
              }
			finish();
		}

LocationListener onLocationChange=new LocationListener() {
	
	public void onLocationChanged(Location fix){
		helper.updateLocation(restaurantId, fix.getLatitude(), fix.getLongitude());
		location.setText(String.valueOf(fix.getLatitude())+","+String.valueOf(fix.getLongitude()));
		locMgr.removeUpdates(onLocationChange);
		
		Toast.makeText(DetailForm.this, "Location Saved", Toast.LENGTH_LONG).show();
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	
};
	
}
