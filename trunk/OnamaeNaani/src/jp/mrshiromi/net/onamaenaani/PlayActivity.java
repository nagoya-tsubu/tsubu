package jp.mrshiromi.net.onamaenaani;

import java.util.Random;

import jp.mrshiromi.net.onamaenaani.util.OnamaeAnimUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

/**
 * 「プレイ」画面
 * 
 * @author k-matsuda
 * 
 */
public class PlayActivity extends BaseActivity {

	private ViewHolder[] mShabons = null;

	private static final int IMG_SHABON = 0;
	private static final int IMG_SHABONX = 1;
	private static final int IMG_ANIMAL = 2;

	private static final int SHOW_ANIMAL_OFFSET = 500; // [msec]

	private static final int MSG_REFRESH = 100;
	private static final int MSG_ACTIVE = 200;
	private static final int MSG_SHOW_ANIMAL = 300;
	private static final int MSG_GO_ANIMAL_ACTIVITY = 400;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MSG_REFRESH:
				refresh();
				break;
			case MSG_ACTIVE:
				isActive = true;
				break;
			case MSG_SHOW_ANIMAL:
				isActive = false;
				showAnimal((ViewHolder) msg.obj);
				break;
			case MSG_GO_ANIMAL_ACTIVITY:
				startAnimalActivity(msg.arg1);
				break;

			default:
				isActive = false;
				mHandler.sendEmptyMessageDelayed(MSG_ACTIVE,
						OnamaeAnimUtil.SHABON_DURATION - 2000);

				startShabonAnimation(msg.what);

				break;
			}
		};
	};

	private void startShabonAnimation(int what) {
		try {
			final ViewHolder Shabon = mShabons[what - 1];
			if (Shabon != null) {
				try {
					((ViewFlipper) Shabon.mShabon)
							.setDisplayedChild(IMG_SHABON);
				} catch (ClassCastException e) {
					e.printStackTrace();
					
					ViewFlipper vf = (ViewFlipper) ((FrameLayout) Shabon.mShabon)
							.findViewById(R.id.flipper);
					vf.setDisplayedChild(IMG_SHABON);
				}
				Shabon.startAnimation();
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

	}

	private final OnClickListener mShabonClickListener = new OnClickListener() {

		@Override
		public synchronized void onClick(View v) {
			if (isActive) {
				ViewHolder shabon = (ViewHolder) v.getTag();
				removeMessages();
				shabon.mShabon.clearAnimation();
				ViewFlipper vf = null;
				try {
					vf = (ViewFlipper) shabon.mShabon;
				} catch (ClassCastException e) {
					vf = (ViewFlipper) shabon.mShabon
							.findViewById(R.id.flipper);
				}
				if (vf.getDisplayedChild() == IMG_ANIMAL) {
					Message msg = mHandler.obtainMessage(
							MSG_GO_ANIMAL_ACTIVITY, shabon.animal, -1);
					mHandler.sendMessage(msg);
					return;
				} else {
					vf.setDisplayedChild(IMG_SHABONX);
					Message msg = mHandler.obtainMessage(MSG_SHOW_ANIMAL,
							shabon);
					mHandler.sendMessageDelayed(msg, SHOW_ANIMAL_OFFSET);
				}
			}
		}
	};

	private void showAnimal(ViewHolder v) {
		ViewFlipper vs = null;
		try {
			vs = (ViewFlipper) v.mShabon;
		} catch (ClassCastException e) {
			vs = (ViewFlipper) v.mShabon.findViewById(R.id.flipper);
		}
		vs.setDisplayedChild(IMG_ANIMAL);
		Message msg = mHandler.obtainMessage(MSG_GO_ANIMAL_ACTIVITY, v.animal,
				-1);
		mHandler.sendMessageDelayed(msg, SHOW_ANIMAL_OFFSET);
	}

	private boolean isActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_play);

		ViewHolder[] shabons = new ViewHolder[] {
				new ViewHolder(findViewById(R.id.Shabon01), AnimationUtils
						.loadAnimation(this, R.anim.shabon), AnimationUtils
						.loadAnimation(this, R.anim.shabon_click)),
				new ViewHolder(findViewById(R.id.Shabon02), AnimationUtils
						.loadAnimation(this, R.anim.shabon), AnimationUtils
						.loadAnimation(this, R.anim.shabon_click)),
				new ViewHolder(findViewById(R.id.Shabon03), AnimationUtils
						.loadAnimation(this, R.anim.shabon), AnimationUtils
						.loadAnimation(this, R.anim.shabon_click)),
				new ViewHolder(findViewById(R.id.Shabon04), AnimationUtils
						.loadAnimation(this, R.anim.shabon), AnimationUtils
						.loadAnimation(this, R.anim.shabon_click)),
				new ViewHolder(findViewById(R.id.Shabon05), AnimationUtils
						.loadAnimation(this, R.anim.shabon), AnimationUtils
						.loadAnimation(this, R.anim.shabon_click)),
				new ViewHolder(findViewById(R.id.Shabon06), AnimationUtils
						.loadAnimation(this, R.anim.shabon), AnimationUtils
						.loadAnimation(this, R.anim.shabon_click)),
				new ViewHolder(findViewById(R.id.Shabon07), AnimationUtils
						.loadAnimation(this, R.anim.shabon), AnimationUtils
						.loadAnimation(this, R.anim.shabon_click)),
				new ViewHolder(findViewById(R.id.Shabon08), AnimationUtils
						.loadAnimation(this, R.anim.shabon), AnimationUtils
						.loadAnimation(this, R.anim.shabon_click)) };
		mShabons = shabons;

		startAnimationTaiyoKumo();
	}

	private void refresh() {

		ViewHolder[] shabons = mShabons;

		int count = shabons.length;
		for (int i = 0; i < count; i++) {
			ViewHolder shabon = shabons[i];
			shabon.mShabon.setVisibility(View.GONE);
			shabon.mShabon.setOnClickListener(mShabonClickListener);
			shabon.mShabon.setTag(shabon);
			shabon.animal = i + 1; // msg.what と同じにするため + 1 ていうか・・・　まいっか
		}

		int start = 1000;
		Random rand = new Random();

		// for (int i = 0; i < 10; i++) {
		for (int j = 1; j <= shabons.length; j++) {
			int randshabon = rand.nextInt() % shabons.length;
			randshabon = randshabon < 0 ? -randshabon : randshabon;// 乱数を正数に
			mHandler.sendEmptyMessageDelayed(randshabon + 1, start);
			start += OnamaeAnimUtil.SHABON_DURATION
					+ OnamaeAnimUtil.SHABON_CLICK_DURATION
					+ OnamaeAnimUtil.SHABON_ALPHA_DURATION + 100;
		}
		// }
		mHandler.sendEmptyMessageDelayed(100, start);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// removeMessages();
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeMessages();
	}

	private void removeMessages() {

		for (int i = 1; i <= mShabons.length; i++)
			mHandler.removeMessages(i);
		mHandler.removeMessages(MSG_REFRESH);

		// 画面に残っている動物を消す
		for (int i = 0; i < mShabons.length; i++) {
			ViewHolder shabon = mShabons[i];
			shabon.mShabon.clearAnimation();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(MSG_REFRESH);
	}

	/**
	 * 猫クリックイベント
	 * 
	 * @param v
	 */
	public void onClickCat(View v) {
		Intent intent = new Intent(this, HitoyasumiActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED) {
			finish();
		}

	}

	/**
	 * 動物画面に遷移
	 * 
	 * @param animal
	 */
	private void startAnimalActivity(int animal) {
		Intent intent = new Intent(this, AnimalActivity.class);
		intent.putExtra(AnimalActivity.EXTRA_ANIMAL_TYPE, animal);
		startActivityForResult(intent, 1);
	}

	private class ViewHolder implements AnimationListener {
		int animal = -1;
		View mShabon = null;
		Animation mAnim = null;
		Animation mClickAnim = null;

		public ViewHolder(View shabon, Animation anim, Animation click) {
			mShabon = shabon;
			mAnim = anim;
			mAnim.setAnimationListener(this);
			mClickAnim = click;
			mClickAnim.setAnimationListener(l);

		}

		public void startAnimation() {
			Log.d("Caldia", "startAnimation");
			if (mShabon != null) {
				// mShabon.setClickable(false);
				// mShabon.setEnabled(false);
				mShabon.setVisibility(View.VISIBLE);
				mShabon.startAnimation(mAnim);
				Log.d("Caldia", "Duration =" + mAnim.getDuration());
			}
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// mShabon.setClickable(true);
			// mShabon.setEnabled(true);
			mShabon.startAnimation(mClickAnim);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

		private final AnimationListener l = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// mShabon.setClickable(false);
				// mShabon.setEnabled(false);
				mShabon.setVisibility(View.GONE);

			}
		};
	}

}
