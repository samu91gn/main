package pisada.fallDetector;

import java.util.Calendar;

import pisada.plotmaker.Data;
import pisada.plotmaker.Plot2d;
import pisada.recycler.CurrentSessionCardAdapter;
import pisada.recycler.SessionListCardAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
/*
 * impostare il layout con tastone grosso sopra ecc e nelle cardview ci vanno le cose varie.
 * 
 * connect dovr� anche inizializzare il timer che manda indietro qui il tempo passato da quando
 * � stato inizializzato il service.
 * 
 * inoltre la lista fall deve essere a sua volta riempita dal service quindi serve altro metodo
 * ancora che funziona come il mandadati dei grafici adesso.
 * 
 * 
 */

public class CurrentSessionActivity extends ActionBarActivity implements ServiceReceiver{

	private Plot2d plotx, ploty, plotz;
	private Intent serviceIntent;
	private Calendar c;
	private int secondsStartGraph;
	RecyclerView rView;
	RecyclerView.Adapter<RecyclerView.ViewHolder> cardAdapter;
	LayoutManager mLayoutManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view; 
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		view = inflater.inflate(R.layout.activity_current_session, null);
		LinearLayout rl = (LinearLayout) view.findViewById(R.id.graphcontainer);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); 

		LinearLayout graphXLayout = (LinearLayout) rl.findViewById(R.id.graphx);
		LinearLayout graphYLayout = (LinearLayout) rl.findViewById(R.id.graphy);
		LinearLayout graphZLayout = (LinearLayout) rl.findViewById(R.id.graphz);
		
		c = Calendar.getInstance();
		
		secondsStartGraph = c.get(Calendar.MINUTE)*60*1000 + c.get(Calendar.SECOND)*1000+ c.get(Calendar.MILLISECOND);

		plotx = new Plot2d(this, new Data(c.get(Calendar.MINUTE)*60*1000 + c.get(Calendar.SECOND)*1000+ c.get(Calendar.MILLISECOND) - secondsStartGraph,0));
		ploty = new Plot2d(this, new Data(c.get(Calendar.MINUTE)*60*1000 + c.get(Calendar.SECOND)*1000+ c.get(Calendar.MILLISECOND) - secondsStartGraph,0));
		plotz = new Plot2d(this, new Data(c.get(Calendar.MINUTE)*60*1000 + c.get(Calendar.SECOND)*1000+ c.get(Calendar.MILLISECOND) - secondsStartGraph,0));
		
		graphXLayout.addView(plotx, lp);
		graphYLayout.addView(ploty, lp);
		graphZLayout.addView(plotz, lp);
		setContentView(rl);
		
		//=======================BLOCCO DA SPOSTARE NEL TASTO START=======================
		
		serviceIntent = new Intent(this, ForegroundService.class);
		String activeServ = Utility.checkLocationServices(this, true);
		serviceIntent.putExtra("activeServices", activeServ);
		startService(serviceIntent);
		ForegroundService.connect(this);
		//====================BLOCCO DA SPOSTARE NEL TASTO START (FINE)===================
		
		
		
		
		
		//INIZIALIZZO RECYCLERVIEW

				rView=(RecyclerView) findViewById(R.id.session_list_recycler);
				rView.setHasFixedSize(true);
				cardAdapter=new CurrentSessionCardAdapter(this);
				rView.setAdapter(cardAdapter);
				mLayoutManager = new LinearLayoutManager(this);
				rView.setLayoutManager(mLayoutManager);


				//BESTIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
				/*AOSDJOIAJDOIAJSMDOIASMDAOIMDOIAMDOIAJMODIAMDOIASJMDAOIM
				asdasd
				asd
				as
				das
				da
				sd
				asd
				as
				d
				a
				sdsa
				a*/
				
				//RIEMPIO RECYCLERVIEW CON TUTTE LE SESSIONI
				
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.current_session, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			if(serviceIntent!=null)
				stopService(serviceIntent);
			return true;
		}
		if(id == R.id.action_settings_2)
		{
			if(serviceIntent!=null)
				startService(serviceIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public void serviceUpdate(float x, float y, float z, long time) {
		
		//==============================================================================
		// TODO qui vanno aggiunti i valori di x, y, z nell'oggetto plot per il grafico.
		// (metodo chiamato automaticamente dal service
		//==============================================================================
		
			c = Calendar.getInstance();
			plotx.pushValue(new Data(time - secondsStartGraph,x));
			plotx.invalidate();
			ploty.pushValue(new Data(time- secondsStartGraph,y));
			ploty.invalidate();
			plotz.pushValue(new Data(time- secondsStartGraph,z));
			plotz.invalidate();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		ForegroundService.disconnect();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		ForegroundService.connect(this);
	}
	
	

	
}
