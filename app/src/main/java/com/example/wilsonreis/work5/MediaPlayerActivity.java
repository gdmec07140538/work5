package com.example.wilsonreis.work5;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
public class MediaPlayerActivity extends Activity{
	private Display currDisplay;
	private SurfaceView surfaceView;
	private SurfaceHolder holder;
	private MediaPlayer player;
	private int vWidth,vHeight;
	private Timer	timer;
	private ImageButton  rew; //����
	private ImageButton  pause;//��ͣ
	private ImageButton  start;//��ʼ
	private ImageButton  ff;//���
	private TextView   play_time;//�Ѳ���ʱ��
	private TextView   all_time;//�ܲ���ʱ��
	private TextView   title;//�����ļ�����
	private SeekBar    seekbar;//������
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); // ���ر���
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        //��ȡ��������ý��·��
        Intent intent = getIntent();
	    Uri uri = intent.getData();
	    //String   mPath="/mnt/sdcard/test.mp3";//����ʱ��Ҳ������ģ��������Ҫ����ռ䣩��������Ĵ洢���Ϸ�һ��test.mp3�ļ�
        String   mPath="";
	    if(uri!=null)
		{
			 mPath = uri.getPath(); //�ⲿ������øó��򣬻��ý��·��
		}else 
		{   
			//�ӳ����ڲ����ļ��д���ѡ���ý��·����
			Bundle localBundle = getIntent().getExtras();
			if(localBundle!=null)
			{
				 String t_path=localBundle.getString("path");
				 if(t_path!=null&&!"".equals(t_path))
				 {
				    mPath=t_path;
				 }
			}
		}
	    //���ص�ǰ�����ļ��ؼ�����
	    title=(TextView)findViewById(R.id.title);
		surfaceView = (SurfaceView)findViewById(R.id.surfaceview);
		rew=(ImageButton)findViewById(R.id.rew);
		pause=(ImageButton)findViewById(R.id.pause);
		start=(ImageButton)findViewById(R.id.start);
		ff=(ImageButton)findViewById(R.id.ff);
		
		play_time=(TextView)findViewById(R.id.play_time);
		all_time=(TextView)findViewById(R.id.all_time);
		seekbar=(SeekBar)findViewById(R.id.seekbar);
		
		//��SurfaceView���CallBack����
		holder = surfaceView.getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {}
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// ��SurfaceView�е�Surface��������ʱ�򱻵���
				//����������ָ��MediaPlayer�ڵ�ǰ��Surface�н��в���
				player.setDisplay(holder);			
				//��ָ����MediaPlayer���ŵ����������ǾͿ���ʹ��prepare����prepareAsync��׼��������				
				player.prepareAsync();
			}
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {}
		});
		//Ϊ�˿��Բ�����Ƶ����ʹ��CameraԤ����������Ҫָ����Buffer����
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
		
		//���濪ʼʵ����MediaPlayer����
		player = new MediaPlayer();
		//���ò�����ɼ�����
		player.setOnCompletionListener(new OnCompletionListener() {			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// ��MediaPlayer������ɺ󴥷�
				if(timer!=null)
				{
					timer.cancel();
					timer=null;
				}
			}
		});
		//����prepare��ɼ�����
		player.setOnPreparedListener(new OnPreparedListener() {			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// ��prepare��ɺ󣬸÷������������������ǲ�����Ƶ
				//����ȡ��video�Ŀ�͸�
				vWidth = player.getVideoWidth();
				vHeight = player.getVideoHeight();
				
				if(vWidth > currDisplay.getWidth() || vHeight > currDisplay.getHeight()){
					//���video�Ŀ���߸߳����˵�ǰ��Ļ�Ĵ�С����Ҫ��������
					float wRatio = (float)vWidth/(float)currDisplay.getWidth();
					float hRatio = (float)vHeight/(float)currDisplay.getHeight();
					//ѡ����һ����������
					float ratio = Math.max(wRatio, hRatio);
					vWidth = (int)Math.ceil((float)vWidth/ratio);
					vHeight = (int)Math.ceil((float)vHeight/ratio);
					//����surfaceView�Ĳ��ֲ���
					surfaceView.setLayoutParams(new LinearLayout.LayoutParams(vWidth, vHeight));
					//Ȼ��ʼ������Ƶ
					player.start();
				}else
				{
					player.start();
				}
				if(timer!=null)
				{
					timer.cancel();
					timer=null;
				}
				//����ʱ����¼���������������ÿ0.5�����һ��
				timer = new Timer();   
			    timer.schedule(new MyTask(), 50,500);
			}
		});
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			
			 //Ȼ��ָ����Ҫ�����ļ���·������ʼ��MediaPlayer
			if(!mPath.equals("")){
			title.setText(mPath.substring(mPath.lastIndexOf("/")+1));			
			player.setDataSource(mPath);			
			}else{
				  AssetFileDescriptor afd = this.getResources().openRawResourceFd(R.drawable.test);
				  player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//��ͣ����
		pause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//������ͣ����pause��Ϊ���أ�start��Ϊ����
				pause.setVisibility(View.GONE);
				start.setVisibility(View.VISIBLE);
				player.pause();
				if(timer!=null)
				{
					timer.cancel();
					timer=null;
				}
			}
		 });
		//���Ų���
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//���˿�ʼ����pause��Ϊ�ɼ���start��Ϊ����
				start.setVisibility(View.GONE);
				pause.setVisibility(View.VISIBLE);
				//��������
				player.start();
				if(timer!=null)
				{
					timer.cancel();
					timer=null;
				}
				//����ʱ����¼���������������ÿ0.5�����һ��
				timer = new Timer();   
			    timer.schedule(new MyTask() , 50,500);
			}
		 });
		 
		//���˲��� ��ÿ�ο���10�� 
		rew.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//�ж��Ƿ����ڲ���
				if(player.isPlaying())
				{
					int currentPosition=player.getCurrentPosition();
					if(currentPosition-10000>0)
					{
						player.seekTo(currentPosition-10000);
					}
				}
			}
		 });
		//���������ÿ�ο��10��
		ff.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//�ж��Ƿ����ڲ���
				if(player.isPlaying())
				{
					int currentPosition=player.getCurrentPosition();
					if(currentPosition+10000<player.getDuration())
					{
						player.seekTo(currentPosition+10000);
					}
				}
			}
		 });
		 //Ȼ������ȡ�õ�ǰDisplay����
		 currDisplay = this.getWindowManager().getDefaultDisplay();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 0, "�ļ���");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==1)
		{
			Intent intent = new Intent(MediaPlayerActivity.this,MyFileActivity.class);
            startActivity(intent);
            finish();
		}
		return super.onOptionsItemSelected(item);
	}
	// ����������
	public  class  MyTask extends  TimerTask
	{
		public void run() {  
	    	   
	    	 Message message = new Message();      
	    	 message.what = 1;
	    	 //������Ϣ���½�������ʱ����ʾ
	    	 handler.sendMessage(message);
	     }  
	}
	// �����������ʱ����ʾ
	private final Handler handler = new Handler(){  
	  public void handleMessage(Message msg) {  
         switch (msg.what) {      
             case 1: 
            	 Time progress = new Time(player.getCurrentPosition());
                 Time allTime = new Time(player.getDuration());
                 String  timeStr=progress.toString();
                 Log.v("progress", timeStr);
                 String  timeStr2=allTime.toString();
                 //�Ѳ���ʱ��
            	 play_time.setText(timeStr.substring(timeStr.indexOf(":")+1));
            	 //��ʱ��
            	 all_time.setText(timeStr2.substring(timeStr.indexOf(":")+1));
                 int progressValue = 0;
                 if(player.getDuration()>0){
                       progressValue = seekbar.getMax()*
                       player.getCurrentPosition()/player.getDuration();
                 }
                 //����������
                 seekbar.setProgress(progressValue);
                 break;      
             }      
             super.handleMessage(msg);  
         }    
    };
}