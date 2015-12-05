package com.example.wilsonreis.work5;

import java.io.File;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyFileActivity extends Activity  {

	// ֧�ֵ�ý���ʽ
	private final String[]FILE_MapTable = {
			".3gp",".mov",".avi", ".rmvb", ".wmv", ".mp3",  ".mp4" };
	private Vector<String> items = null; // items�������ʾ������
	private Vector<String> paths = null; // paths������ļ�·��
	private Vector<String> sizes = null; // sizes���ļ���С
	private String   rootPath = "/mnt/sdcard"; //��ʼ�ļ���
	private EditText pathEditText;  // ·��
	private Button   queryButton;  //��ѯ��ť
	private ListView  fileListView;//�ļ��б�
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle); 
		this.setTitle("��ý���ļ����");
		setContentView(R.layout.myfile);
		//��myfile.xml�ҵ���Ӧ��Ԫ��
		pathEditText = (EditText) findViewById(R.id.path_edit);
		queryButton = (Button) findViewById(R.id.qry_button);
		fileListView= (ListView) findViewById(R.id.file_listview);
		//��ѯ��ť�¼�
		queryButton.setOnClickListener( new Button.OnClickListener() {
			public void onClick(View arg0) {
				File file = new File(pathEditText.getText().toString());
				if (file.exists()) {
					if (file.isFile()) {
						//�����ý���ļ�ֱ�Ӵ򿪲���
						openFile(pathEditText.getText().toString());
					} else {
						//�����Ŀ¼��Ŀ¼���ļ�
						getFileDir(pathEditText.getText().toString());
					}
				} else {
					Toast.makeText(MyFileActivity.this, "�Ҳ�����λ��,��ȷ��λ���Ƿ���ȷ!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		//����ListItem�����ʱҪ���Ķ���
		fileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				fileOrDir(paths.get(position)); 
			}
		});
		
		//��Ĭ���ļ���
		getFileDir(rootPath);
	}
	/**
	 * ��д���ؼ�����:������һ���ļ���
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// �Ƿ񴥷�����Ϊback��
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pathEditText = (EditText) findViewById(R.id.path_edit);
			File file = new File(pathEditText.getText().toString());
			if (rootPath.equals(pathEditText.getText().toString().trim())) {
				return super.onKeyDown(keyCode, event);
			} else {
				getFileDir(file.getParent());
				return true;
			}
			//�������back��������Ӧ
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	/**
	 * �����ļ�����Ŀ¼�ķ���
	 */
	private void fileOrDir(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			getFileDir(file.getPath());
		} else {
			openFile(path);
		}
	}
	/**
	 * ȡ���ļ��ṹ�ķ���
	 */
	private void getFileDir(String filePath) {
		/* ����Ŀǰ����·�� */
		pathEditText.setText(filePath);
		items = new Vector<String>();
		paths = new Vector<String>();
		sizes = new Vector<String>();
		File f = new File(filePath);
		File[] files = f.listFiles();
		if (files != null) {
			/* �������ļ����ArrayList�� */
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					items.add(files[i].getName());
					paths.add(files[i].getPath());
					sizes.add("");
				}
			}

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					String fileName = files[i].getName();
					int index = fileName.lastIndexOf(".");
					if (index > 0) {
						String endName = fileName.substring(index,
								fileName.length()).toLowerCase();
						String type = null;
						for (int x = 0; x < FILE_MapTable.length; x++) {
							// ֧�ֵĸ�ʽ���Ż����ļ����������ʾ
							if (endName.equals(FILE_MapTable[x])) {
								type = FILE_MapTable[x];
								break;
							}
						}
						if (type != null) {
							items.add(files[i].getName());
							paths.add(files[i].getPath());
							sizes.add(files[i].length()+"");
						}
					}
				}
			}
		}
		/* ʹ���Զ����FileListAdapter�������ݴ���ListView */
		fileListView.setAdapter(new FileListAdapter(this, items));
	}
	/**
	 * ��ý���ļ�
	 * @param f
	 */
	private void openFile(String  path) {
		
		 //��ý�岥����
		 Intent intent = new Intent(MyFileActivity.this, MediaPlayerActivity.class);
         intent.putExtra("path",path);
         startActivity(intent);
		 finish();
	}
	/**
	 *ListView�б�������
	 */
	class  FileListAdapter  extends  BaseAdapter
	{

		private Vector<String> items = null; // items�������ʾ������
		private MyFileActivity myFile;  
		public FileListAdapter(MyFileActivity myFile, Vector<String> items)
		{
			this.items=items;
			this.myFile=myFile;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.elementAt(position);
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return items.size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		     	// TODO Auto-generated method stub
		    if(convertView==null)
	        {  
		       //�����б����file_item.xml
			   convertView = myFile.getLayoutInflater()
			   .inflate(R.layout.file_item, null);
	        }
		    //�ļ�����
	    	TextView   name = (TextView) convertView.findViewById(R.id.name); 
	    	//ý���ļ�����
	    	ImageView   music=(ImageView)convertView.findViewById(R.id.music);
	    	//�ļ�������
	    	ImageView   folder=(ImageView)convertView.findViewById(R.id.folder);
	    	name.setText(items.elementAt(position));
    		if(sizes.elementAt(position).equals(""))
    		{
    			//����ý��ͼ�꣬��ʾ�ļ���ͼ��
    			music.setVisibility(View.GONE);
    			folder.setVisibility(View.VISIBLE);
    		}else
    		{
    			//�����ļ���ͼ�꣬��ʾý��ͼ��
    			folder.setVisibility(View.GONE);
    			music.setVisibility(View.VISIBLE);
    		}
	        return convertView;	
		}
	}
}
