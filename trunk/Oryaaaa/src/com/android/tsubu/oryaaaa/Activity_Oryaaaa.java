package com.android.tsubu.oryaaaa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * simeji�}�b�V�����[�� (�u�E�ցE)�u�����[
 * 
 * @author TAN(@tan1234jp)
 * 
 *         ���ϔ� simeji�}�b�V�����[�� �iɁ��ށ��j� � ���������[
 * @author �O��(@mrshiromi)
 * 
 */
public class Activity_Oryaaaa extends Activity {

    private static final String     ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";    //simeji����N��
    private static final String     REPLACE_KEY = "replace_key";                    //���m�蕶����
    private static final String     GAOH_STRING = "�iɁ��ށ��j� � ���������[";             //�iɁ��ށ��j� � ���������[

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //�������g�̃C���e���g���擾���A�ǂ̃A�N�V�����ŌĂяo���ꂽ����
        //�擾����
        Intent  it = getIntent();
        String action = it.getAction();
        
        //simeji����Ăяo���ꂽ���ǂ����𔻒肷��
        if((null != action) && (true == ACTION_INTERCEPT.equals(action))) {
                //simeji����Ăяo���ꂽ�Ƃ��A���m�蕶����̌�Ɂu �iɁ��ށ��j� � ���������[ �v��
                //�ǉ�����
                StringBuffer    sb = new StringBuffer();
                sb.append(it.getStringExtra(REPLACE_KEY));      //���m�蕶������擾����

                //���m�蕶���񂪑��݂���ꍇ�́A���m�蕶����̌�ɔ��p�X�y�[�X��}������
                if(0 < sb.length()) {
                        sb.append(" ");
                }
                //(�u�E�ցE)�u�����[
                sb.append(GAOH_STRING);
                //�Ō�ɔ��p�X�y�[�X��}������
                sb.append(" ");

                //�ǉ������������simeji�ɓn��
                Intent data = new Intent();
                data.putExtra(REPLACE_KEY, sb.toString());
                setResult(RESULT_OK, data);
        }
        //�A�v�����I������
        finish();
    }
}