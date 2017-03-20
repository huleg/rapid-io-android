package io.rapid.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.rapid.Rapid;
import io.rapid.RapidSubscription;
import io.rapid.RapidWrapper;
import io.rapid.sample.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

	public static final String COLLECTIONS_CARS = "cars";
	private static final String RAPID_API_KEY = "sdafh87923jweql2393rfksad";
	private RapidSubscription mSubscription;
	private ActivityMainBinding mBinding;
	private MainViewModel mViewModel;


	private static void log(String message) {
		Log.d("Rapid Sample", message);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewModel = new MainViewModel();
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		mBinding.setViewModel(mViewModel);

		Rapid.initialize(RAPID_API_KEY);
//		Rapid.getInstance().setJsonConverter(new RapidJacksonConverter());


		mSubscription = Rapid.getInstance().collection(COLLECTIONS_CARS, Car.class)
				.subscribe((carCollection) -> {
					log(carCollection.toString());

					List<Car> cars = new ArrayList<>();
					for(RapidWrapper<Car> carRapidWrapper : carCollection) {
						cars.add(carRapidWrapper.getBody());
					}
					mViewModel.items.update(cars);
				});
	}


	@Override
	protected void onDestroy() {
		mSubscription.unsubscribe();
		super.onDestroy();
	}


	public void addItem(View view) {
		Car newCar = new Car(new Random().nextInt());

		Rapid.getInstance()
				.collection(COLLECTIONS_CARS, Car.class)
				.newDocument()
				.mutate(newCar)
				.onSuccess(() -> {
					log("Mutation successful");
				})
				.onError(error -> {
					log("Mutation error");
					error.printStackTrace();
				});
	}
}
