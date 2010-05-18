package com.tsubu.bubble;

/**
 * 各シーン定義
 */
public abstract class Scene
{
	// 終了処理
	abstract void finish();
	
	// 描画処理
	abstract void draw();
	
	// 動作処理
	abstract SceneResult action();
}
