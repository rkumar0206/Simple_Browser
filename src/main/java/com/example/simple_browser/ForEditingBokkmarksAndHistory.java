package com.example.simple_browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.textfield.TextInputLayout;

public class ForEditingBokkmarksAndHistory extends AppCompatActivity implements View.OnClickListener {

    TextView toolbar_title;
    ImageView save_button, close_edit_bookmark;

    TextInputLayout title_edit, link_edit;
    long id;
    RelativeLayout edit_box;
    String title;
    String link;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_editing_bokkmarks_and_history);

        findingIds();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();            //intent coming from MainActivity


        if (b != null) {
            id = b.getLong(MainActivity.Key_book_id);
            title = b.getString(MainActivity.Key_book_title);
            link = b.getString(MainActivity.Key_book_url);

        }

        title_edit.getEditText().setText(title);
        link_edit.getEditText().setText(link);

        save_button.setOnClickListener(this);

        //title_edit.setOnClickListener(this);
        //link_edit.setOnClickListener(this);
        close_edit_bookmark.setOnClickListener(this);


        title_edit.getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (title_edit.getEditText().getRight() - title_edit.getEditText().getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        title_edit.getEditText().getText().clear();
                        // your action here

                        return true;
                    }
                }
                return false;
            }
        });


        link_edit.getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (link_edit.getEditText().getRight() - link_edit.getEditText().getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        link_edit.getEditText().setText("https://www.");
                        // your action here

                        return true;
                    }
                }
                return false;
            }
        });

    }

    void findingIds() {

        toolbar_title = findViewById(R.id.toolbar_title_textview);
        save_button = findViewById(R.id.save_edited);

        title_edit = findViewById(R.id.text_title);
        link_edit = findViewById(R.id.text_link);
        edit_box = findViewById(R.id.r);
        close_edit_bookmark = findViewById(R.id.close_edit_bookmark);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.save_edited) {

            if (!link_edit.getEditText().getText().toString().startsWith("https://www.")) {
                Toast.makeText(this, "Link should start with https://wwww. and end with any domain!!", Toast.LENGTH_SHORT).show();
            } else if (!title_edit.getEditText().getText().toString().isEmpty() && !link_edit.getEditText().getText().toString().isEmpty()) {

                Bookmar_functions bookmar_functions = new Bookmar_functions(this);
                bookmar_functions.update(title_edit.getEditText().getText().toString().trim(), link_edit.getEditText().getText().toString().trim(), id);
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

               /* if (MainActivity.rl2.getVisibility() == View.VISIBLE) {

                    Bookmar_functions bookmar_function = new Bookmar_functions(this);
                    boolean f = bookmar_function.search(MainActivity.webView.getUrl());
                    if (f) {
                        MainActivity.add_bookmark.setImageResource(R.drawable.ic_star_border_black);
                    }

                }

                */


                finish();
            } else {

                if (title_edit.getEditText().getText().toString().isEmpty()) {
                    title_edit.setError("Field can't be empty");
                }
                if (link_edit.getEditText().getText().toString().isEmpty()) {
                    link_edit.setError("Field can't be empty");
                }
                YoYo.with(Techniques.Tada).duration(800).repeat(1).playOn(edit_box);
                Toast.makeText(this, "No fields should be empty!!!", Toast.LENGTH_SHORT).show();
            }

        }


        if (v.getId() == R.id.close_edit_bookmark) {
            finish();
        }


    }


}
