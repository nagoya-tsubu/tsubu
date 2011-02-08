package jp.mrshiromi.net.onamaenaani;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 「動物」画面
 * 
 * @author k-matsuda
 * 
 */
public class AnimalActivity extends BaseActivity implements OnClickListener {

	public static final String EXTRA_ANIMAL_TYPE = "EXTRA_ANIMAL_TYPE";

	private static int SOUND_ID_MAT[] = 
	{
		0,
		R.raw.animals01,
		R.raw.animals02,
		R.raw.animals03,
		R.raw.animals04,
		R.raw.animals05,
		R.raw.animals06,
		R.raw.animals07,
		R.raw.animals08
	};
	
	private static int ANIMAL_ID_MAT[] = 
	{
		0,
		R.drawable.animalp01,
		R.drawable.animalp02,
		R.drawable.animalp03,
		R.drawable.animalp04,
		R.drawable.animalp05,
		R.drawable.animalp06,
		R.drawable.animalp07,
		R.drawable.animalp08		
	};
	
	private static int FUKIDASHI_ID_MAT[] = 
	{
		0,
		R.drawable.anamae01,
		R.drawable.anamae02,
		R.drawable.anamae03,
		R.drawable.anamae04,
		R.drawable.anamae05,
		R.drawable.anamae06,
		R.drawable.anamae07,
		R.drawable.anamae08		
	};
	
	private static int sound_id=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_animal);

		Intent intent = getIntent();

		int animal = sound_id= intent.getIntExtra(EXTRA_ANIMAL_TYPE, -1);

		ImageView img = (ImageView) findViewById(R.id.ImageViewAnimal);

		img.setBackgroundResource(ANIMAL_ID_MAT[animal]);

		img.setOnClickListener(this);
		
		ImageView fuk = (ImageView) findViewById(R.id.ImageViewFukidashi);
		
		fuk.setBackgroundResource(FUKIDASHI_ID_MAT[animal]);
		
		fuk.setOnClickListener(this);

		super.startAnimationTaiyoKumo();

		setResult(RESULT_CANCELED);

	}

	public void onClickBack(View v) {
		setResult(RESULT_OK);
		finish();
	}

	private synchronized void playSound(int resid) {
		//タッチしても何も起こらないように設定
		disableOnClick();
		MediaPlayer player = MediaPlayer.create(this, resid);
		player.start();
		//音声が終了したときに呼び出されて、OnClickListenerを再設定する
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				//OnClickListenerの再設定
				enableOnClick();
			}
		});
	}

	/**
	 * 動物と吹き出しのイメージビューにOnClickListener を再設定
	 */
	private void enableOnClick(){
		ImageView img = (ImageView) findViewById(R.id.ImageViewAnimal);
		ImageView fuk = (ImageView) findViewById(R.id.ImageViewFukidashi);
		img.setOnClickListener(this);
		fuk.setOnClickListener(this);
	}	
	/**
	 * 動物と吹き出しのイメージビューのOnClickListener にnullを設定。タッチしても何も起こらない
	 */
	private void disableOnClick(){
		ImageView img = (ImageView) findViewById(R.id.ImageViewAnimal);
		ImageView fuk = (ImageView) findViewById(R.id.ImageViewFukidashi);
		img.setOnClickListener(null);
		fuk.setOnClickListener(null);
	}
	
	@Override
	public void onClick(View v) {
		playSound(SOUND_ID_MAT[sound_id]);
	}
}
