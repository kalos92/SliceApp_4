package it.polito.mad17.viral.sliceapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Partecipant_Group extends AppCompatActivity {

    private Gruppo g;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private ValueEventListener listener;
    private ValueEventListener listener_2;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private String gID;
    private static final int PICK_IMAGE_ID = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partecipant__group);


        Bundle extra = getIntent().getExtras();

        if(extra!=null){
            g= (Gruppo) extra.getSerializable("Gruppo");
        }

        gID = g.getGroupID();
        RecyclerView mylist = (RecyclerView) findViewById(R.id.partecipanti);
        ArrayList<Persona> persone = new ArrayList<Persona>();
        persone.addAll(g.obtainPartecipanti().values());
        Partecipanti_adapter adapter = new Partecipanti_adapter(persone,getBaseContext(),g.getPolicy());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mylist.setLayoutManager(llm);
        mylist.setAdapter(adapter);

        final ImageView propic = (ImageView) findViewById(R.id.group_pic);
        if(g.getUri()!=null)
             Picasso.with(getBaseContext()).load(g.getUri()).placeholder(R.drawable.img_user).transform(new RoundedTransformation(500, 1)).into(propic);
        else
            Picasso.with(getBaseContext()).load(R.drawable.img_gruppi).transform(new RoundedTransformation(500, 1)).into(propic);


        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(propic, propic.getDrawable());
                mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
            }
        });

        listener = groups_ref.child(g.getGroupID()).child("uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uri = (String) dataSnapshot.getValue();
                Picasso.with(getBaseContext()).load(uri).placeholder(R.drawable.img_gruppi).transform(new RoundedTransformation(500, 1)).into(propic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final TextView tv1 = (TextView) findViewById(R.id.name_group);

        listener_2 = groups_ref.child(g.getGroupID()).child("groupName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String str = (String) dataSnapshot.getValue();
                tv1.setText(str);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageButton btn1 = (ImageButton) findViewById(R.id.change_name) ;
        ImageButton btn2 = (ImageButton) findViewById(R.id.change_propic);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(g.getGroupCreator().equals(SliceAppDB.getUser().getTelephone()))
                showInputDialog();
                else
                    Toast.makeText(getBaseContext(),"Only the creator of this group can change the name", Toast.LENGTH_LONG).show();

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getBaseContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        groups_ref.child(g.getGroupID()).child("groupName").removeEventListener(listener);
        groups_ref.child(g.getGroupID()).child("uri").removeEventListener(listener_2);
        finish();
    }

        public static class PartecipantsHolder extends RecyclerView.ViewHolder {

            ImageView propic;
            TextView namep;
            TextView policy;

            public PartecipantsHolder(View itemView) {
                super(itemView);
                propic = (ImageView) itemView.findViewById(R.id.propic_show);
                namep = (TextView) itemView.findViewById(R.id.person_show);
                policy = (TextView) itemView.findViewById(R.id.pol_show);

            }
        }
    private void zoomImageFromThumb(final View thumbView, Drawable imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image_2);
        expandedImageView.setImageDrawable(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.c2).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }


    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Partecipant_Group.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Partecipant_Group.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.change_gname);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!editText.getText().toString().equals("") && !editText.getText().toString().isEmpty() && editText.getText().toString().length()<15) {
                            groups_ref.child(gID).child("groupName").setValue(editText.getText().toString());
                            ArrayList<Persona> persone = new ArrayList<Persona>();
                            persone.addAll(g.obtainPartecipanti().values());
                            UpName up = new UpName(persone,g.getGroupID(),editText.getText().toString());
                            up.execute();

                        }else
                            editText.setError("Invalid Name, it must be less than 15 characters");
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap b = ImagePicker.getImageFromResult(this, resultCode, data);
                if(b!=null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    ArrayList<Persona> persone = new ArrayList<Persona>();
                    persone.addAll(g.obtainPartecipanti().values());
                    UpImg up = new UpImg(b,gID,persone,getBaseContext());
                    up.execute();

                }
                break;

}}


}
