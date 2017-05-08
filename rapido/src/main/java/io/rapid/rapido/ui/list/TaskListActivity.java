package io.rapid.rapido.ui.list;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;

import io.rapid.rapido.R;
import io.rapid.rapido.databinding.ActivityTaskListBinding;
import io.rapid.rapido.databinding.DialogEditTaskBinding;
import io.rapid.rapido.databinding.DialogOrderBinding;
import io.rapid.rapido.model.Task;
import io.rapid.rapido.ui.edit.EditTaskViewModel;


public class TaskListActivity extends AppCompatActivity {


	private ActivityTaskListBinding mBinding;
	private TaskListViewModel mViewModel;


	private static void log(String message) {
		Log.d("Rapid Sample", message);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_search:
				TransitionManager.beginDelayedTransition((ViewGroup) mBinding.getRoot());
				mViewModel.searching.set(!mViewModel.searching.get());
				return true;
			case R.id.menu_filter:
				// TODO
				return true;
			case R.id.menu_order:
				showOrderDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}


	public void showAddDialog() {
		showEditDialog(null, null);
	}


	public void showEditDialog(String taskId, Task task) {
		BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.Theme_Design_Light_BottomSheetDialog_TranslucentStatus);
		DialogEditTaskBinding dialogBinding = DialogEditTaskBinding.inflate(LayoutInflater.from(this));

		EditTaskViewModel editTaskViewModel = new EditTaskViewModel(dialog, taskId, task, mViewModel);

		dialogBinding.setViewModel(editTaskViewModel);
		dialog.setContentView(dialogBinding.getRoot());
		BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) dialogBinding.getRoot().getParent());
		bottomSheetBehavior.setSkipCollapsed(true);
		dialog.show();
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
	}


	public void showOrderDialog() {
		BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.Theme_Design_Light_BottomSheetDialog_TranslucentStatus);
		DialogOrderBinding dialogBinding = DialogOrderBinding.inflate(LayoutInflater.from(this));


		OrderViewModel orderViewModel = new OrderViewModel(
				Arrays.asList(getResources().getStringArray(R.array.order_values)),
				mViewModel.orderProperty.get(),
				mViewModel.orderSorting.get(),
				(orderProperty, sorting) -> {
					mViewModel.orderProperty.set(orderProperty);
					mViewModel.orderSorting.set(sorting);
				}
		);

		dialogBinding.setViewModel(orderViewModel);
		dialog.setContentView(dialogBinding.getRoot());
		dialog.show();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setup ViewModel
		mViewModel = new TaskListViewModel();

		// setup views
		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_task_list);
		setSupportActionBar(mBinding.toolbar);

		mBinding.setView(this);
		mBinding.setViewModel(mViewModel);

		mViewModel.initialize(this);
		mViewModel.onViewAttached();

		initItemTouchHelper();
	}


	@Override
	protected void onDestroy() {
		mViewModel.onViewDetached();
		super.onDestroy();
	}


	private void initItemTouchHelper() {
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
			@Override
			public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
			}


			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				return false;
			}


			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
				mViewModel.deleteTask(viewHolder.getAdapterPosition());
			}
		});
		itemTouchHelper.attachToRecyclerView(mBinding.list);
	}


}
