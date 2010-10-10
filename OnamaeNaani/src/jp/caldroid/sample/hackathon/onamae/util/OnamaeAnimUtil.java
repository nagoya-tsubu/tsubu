package jp.caldroid.sample.hackathon.onamae.util;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class OnamaeAnimUtil {

	/**
	 * スプラッシュロゴアニメーション
	 * 
	 * @return
	 */
	public static Animation createSplashLogoAnimation() {
		AnimationSet set = new AnimationSet(false);

		ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(1000);
		sa.setRepeatCount(5);
		sa.setRepeatMode(Animation.REVERSE);

		set.addAnimation(sa);
		set.setInterpolator(new OvershootInterpolator(1.5f));

		return set;
	}

	/**
	 * 太陽アニメーションを作成
	 * 
	 * @param context
	 * @return
	 */
	public static Animation createTaiyoAnimation(Context context) {
		AnimationSet set = new AnimationSet(true);

		RotateAnimation ra = new RotateAnimation(-8, 8,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(6000);
		ra.setRepeatMode(Animation.REVERSE);
		ra.setRepeatCount(10);

		ScaleAnimation sa = new ScaleAnimation(0.95f, 1.05f, 0.95f, 1.05f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(6000);
		sa.setRepeatMode(Animation.REVERSE);
		sa.setRepeatCount(10);

		set.addAnimation(ra);
		set.addAnimation(sa);
		set.setFillAfter(true);

		return set;
	}

	/**
	 * 雲アニメーションを作成
	 * 
	 * @param context
	 * @return
	 */
	public static Animation createKumoAnimation(Context context) {
		AnimationSet set = new AnimationSet(true);

		ScaleAnimation sa = new ScaleAnimation(0.98f, 1.02f, 0.98f, 1.02f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(12000);
		sa.setRepeatMode(Animation.REVERSE);
		sa.setRepeatCount(5);

		TranslateAnimation ta = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -0.05f, Animation.RELATIVE_TO_SELF,
				0.05f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f);
		ta.setDuration(6000);
		ta.setRepeatMode(Animation.REVERSE);
		ta.setRepeatCount(10);

		set.addAnimation(sa);
		set.addAnimation(ta);
		set.setFillAfter(true);

		return set;
	}

	public static final int SHABON_DURATION = 10000;
	public static final int SHABON_ALPHA_DURATION = 500;

	/**
	 * しゃぼんだま
	 * 
	 * @return
	 */
	public static Animation createShabonAnimation() {
		AnimationSet set = new AnimationSet(false);

		AlphaAnimation aa = new AlphaAnimation(0f, 1f);
		aa.setDuration(SHABON_ALPHA_DURATION * 4);

		TranslateAnimation ta1 = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -0.1f,
				Animation.RELATIVE_TO_PARENT, 0.1f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		ta1.setDuration(SHABON_DURATION / 5);
		ta1.setRepeatMode(Animation.REVERSE);
		ta1.setRepeatCount(SHABON_DURATION / (SHABON_DURATION / 5) - 1);

		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(SHABON_DURATION - SHABON_ALPHA_DURATION);
		// sa.setRepeatCount(1);
		// sa.setRepeatMode(Animation.REVERSE);

		TranslateAnimation ta2 = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 0.8f, // 猫の上あたり
				Animation.RELATIVE_TO_PARENT, 0f);
		ta2.setDuration(SHABON_DURATION + SHABON_ALPHA_DURATION);

		AlphaAnimation aa2 = new AlphaAnimation(1f, 0f);
		aa2.setDuration(SHABON_ALPHA_DURATION);
		aa2.setStartOffset(SHABON_DURATION - SHABON_ALPHA_DURATION);

		TranslateAnimation ta3 = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				-0.1f, Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, 0f);
		ta3.setDuration(SHABON_DURATION / 5);
		ta3.setStartOffset(SHABON_DURATION);

		set.addAnimation(aa);
		set.addAnimation(ta1);
		set.addAnimation(sa);
		set.addAnimation(ta2);
		// set.addAnimation(aa2);
		set.addAnimation(ta3);
		set.setFillAfter(true);

		return set;
	}

	public static final int SHABON_CLICK_DURATION = 3000;

	public static Animation createShabonClickAnimation() {
		AnimationSet set = new AnimationSet(true);

		AlphaAnimation aa = new AlphaAnimation(0.5f, 1f);
		aa.setDuration(SHABON_CLICK_DURATION / 10);
		aa.setRepeatMode(Animation.REVERSE);
		aa.setRepeatCount(SHABON_CLICK_DURATION / (SHABON_CLICK_DURATION / 10));

		ScaleAnimation sa = new ScaleAnimation(0.99f, 1.0f, 0.99f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(SHABON_CLICK_DURATION / 10);
		sa.setRepeatMode(Animation.REVERSE);
		sa.setRepeatCount(SHABON_CLICK_DURATION / (SHABON_CLICK_DURATION / 10));

		AlphaAnimation aa2 = new AlphaAnimation(1f, 0f);
		aa2.setDuration(SHABON_CLICK_DURATION / 10);
		aa2.setStartOffset(SHABON_CLICK_DURATION - SHABON_CLICK_DURATION / 10);

		set.addAnimation(aa);
		set.addAnimation(sa);
		set.addAnimation(aa2);
		set.setFillAfter(true);

		return set;
	}
}
