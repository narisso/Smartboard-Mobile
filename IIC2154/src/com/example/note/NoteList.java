package com.example.note;

import com.iic2154.R;
import com.samsung.spensdk.SCanvasView;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class NoteList extends ListActivity {
	
	public static SCanvasView mSCanvas;
	
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
	
    private static final int DELETE_ID = Menu.FIRST;
	private int mNoteNumber = 1;
	
	public static int Notes_Count = 0;
	
	private NotesDbAdapter mDbHelper;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);
		mDbHelper = new NotesDbAdapter (this);
		mDbHelper.open();
		
		fillData();				
		registerForContextMenu(getListView());
		Button addnote = (Button)findViewById(R.id.addnotebutton);
		addnote.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				createNote();
				}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notelist_menu, menu);
		return true;		
	}
	
	private void createNote() {
		Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);    	
    }
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

	private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);
        

        String[] from = new String[] { NotesDbAdapter.KEY_TITLE ,NotesDbAdapter.KEY_DATE};
        int[] to = new int[] { R.id.text1 ,R.id.date_row};
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
            new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        setListAdapter(notes);
        
        for(int i=0; i<Notes_Count; ++i){
        	String s1 = NotesDbAdapter.KEY_TITLE + "-" + (i+1);
        	String s2 = NotesDbAdapter.KEY_DATE + "-" + (i+1);
        	String s3 = NotesDbAdapter.KEY_BODY + "-" + (i+1);
        	
        	mSCanvas.clearStringExtra(s1);
        	mSCanvas.clearStringExtra(s2);
        	mSCanvas.clearStringExtra(s3);
        }
        
        Cursor notesCursor2 = mDbHelper.fetchAllNotes();
        Notes_Count = notesCursor2.getCount();
        for(int i=0; i < notesCursor2.getCount(); i++){
        	
        	notesCursor2.moveToNext();

        	String s1 = NotesDbAdapter.KEY_TITLE + "-" + (i+1);
        	String c1 = notesCursor2.getString(1);
        	
        	String s2 = NotesDbAdapter.KEY_DATE + "-" + (i+1);
        	String c2 = notesCursor2.getString(3);
        	
        	String s3 = NotesDbAdapter.KEY_BODY + "-" + (i+1);
        	String c3 = notesCursor2.getString(2);
        	
        	mSCanvas.putExtra(s1, c1);//notesCursor2.getString(0));
        	mSCanvas.putExtra(s2, c2);//notesCursor2.getString(1));
        	mSCanvas.putExtra(s3, c3);//notesCursor2.getString(2));
        }
        mSCanvas.putExtra("size-note", Notes_Count);
    }
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();        
    }   
    
}
