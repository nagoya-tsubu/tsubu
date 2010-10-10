package jp.caldroid.sample.hackathon.onamae;

import android.content.Intent;
import android.media.MediaPlayer;
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

		super.startAnimationTaiyoKumo();

		setResult(RESULT_CANCELED);
		
		playSound(SOUND_ID_MAT[sound_id]);

	}

	public void onClickBack(View v) {
		setResult(RESULT_OK);
		finish();
	}

	private void playSound(int resid) {
		MediaPlayer player = MediaPlayer.create(this, resid);
		player.start();
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		playSound(SOUND_ID_MAT[sound_id]);
	}
}
