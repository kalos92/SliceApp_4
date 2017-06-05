package it.polito.mad17.viral.sliceapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ExpenseDetails extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private Spesa s;
    private Gruppo gruppo;
    private LinearLayoutManager mLayoutManager;
    final DatabaseReference user_ref = rootRef.child("users_prova");

    private boolean isRemoved=false;
    private  ValueEventListener listener;
    private String contestationID;
    private String contestator;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_details);

        Bundle extra = getIntent().getExtras();
        if(extra!= null) {
            s= (Spesa) extra.get("Spesa");
            gruppo = (Gruppo) extra.get("Gruppo");
        }

        listener = groups_ref.child(gruppo.getGroupID()).child("spese").child(s.getExpenseID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Spesa s2;
                s2=dataSnapshot.getValue(Spesa.class);
                if(!isRemoved) {
                    if (s2.getContested() == true && s.getFullypayed().values().size()!=gruppo.getN_partecipanti()-1) {
                        CardView cd = (CardView) findViewById(R.id.status_card);
                        cd.setBackgroundColor(Color.argb(255, 248, 148, 6));
                        ImageView status_1 = (ImageView) findViewById(R.id.status_pic1);
                        status_1.setImageResource(R.drawable.status_1);
                        ImageView status_2 = (ImageView) findViewById(R.id.status_pic2);
                        status_2.setImageResource(R.drawable.status_1);
                        TextView tx_status = (TextView) findViewById(R.id.status_txt);
                        tx_status.setText("CONTESTED");

                    } else if (s2.getContested() != true && s.getFullypayed().values().size()!=gruppo.getN_partecipanti()-1) {
                        CardView cd = (CardView) findViewById(R.id.status_card);
                        cd.setBackgroundColor(Color.argb(255, 238, 90, 75));
                        ImageView status_1 = (ImageView) findViewById(R.id.status_pic1);
                        status_1.setImageResource(R.drawable.status_2);
                        ImageView status_2 = (ImageView) findViewById(R.id.status_pic2);
                        status_2.setImageResource(R.drawable.status_2);
                        TextView tx_status = (TextView) findViewById(R.id.status_txt);
                        tx_status.setText("NOT PAYED");
                    } else if (s2.getContested() != true && s.getFullypayed().values().size()==gruppo.getN_partecipanti()-1) {
                        CardView cd = (CardView) findViewById(R.id.status_card);
                        cd.setBackgroundColor(Color.argb(255, 135, 211, 124));
                        ImageView status_1 = (ImageView) findViewById(R.id.status_pic1);
                        status_1.setImageResource(R.drawable.status_3);
                        ImageView status_2 = (ImageView) findViewById(R.id.status_pic2);
                        status_2.setImageResource(R.drawable.status_3);
                        TextView tx_status = (TextView) findViewById(R.id.status_txt);
                        tx_status.setText("FULL PAYED");
                    } else if (s2.getContested() == true && s.getFullypayed().values().size()==gruppo.getN_partecipanti()-1) {

                        CardView cd = (CardView) findViewById(R.id.status_card);
                        cd.setBackgroundColor(Color.argb(255, 248, 148, 6));
                        ImageView status_1 = (ImageView) findViewById(R.id.status_pic1);
                        status_1.setImageResource(R.drawable.status_1);
                        ImageView status_2 = (ImageView) findViewById(R.id.status_pic2);
                        status_2.setImageResource(R.drawable.status_1);
                        TextView tx_status = (TextView) findViewById(R.id.status_txt);
                        tx_status.setText("CONTESTED");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ImageButton thumbview = (ImageButton) findViewById(R.id.buyerPic);
        if(s.getImg()!=null){
            Picasso.with(getBaseContext()).load(s.getImg()).placeholder(s.getCat().getImg()).into(thumbview);
            thumbview.getDrawable();
            thumbview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(thumbview, thumbview.getDrawable());
                    mShortAnimationDuration = getResources().getInteger(
                            android.R.integer.config_shortAnimTime);
                }
            });}
        else {

            Picasso.with(getBaseContext()).load(s.getCat().getImg()).into(thumbview);
            thumbview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb_res(thumbview, R.drawable.no_img);
                mShortAnimationDuration = getResources().getInteger(
                        android.R.integer.config_shortAnimTime);
            }
        });
        }
        Toolbar t = (Toolbar) findViewById(R.id.expenseToolbar);
        t.setTitle(s.getNome_spesa());

        if(s.getContested()==true && s.getFullypayed().values().size()!=gruppo.getN_partecipanti()-1){
            CardView cd = (CardView) findViewById(R.id.status_card);
            cd.setBackgroundColor(Color.argb(255,248,148,6));
            ImageView status_1 = (ImageView) findViewById(R.id.status_pic1);
            status_1.setImageResource(R.drawable.status_1);
            ImageView status_2 = (ImageView) findViewById(R.id.status_pic2);
            status_2.setImageResource(R.drawable.status_1);
            TextView tx_status = (TextView) findViewById(R.id.status_txt);
            tx_status.setText("CONTESTED");

        }
        else if(s.getContested()!=true && s.getFullypayed().values().size()!=gruppo.getN_partecipanti()-1){
            CardView cd = (CardView) findViewById(R.id.status_card);
            cd.setBackgroundColor(Color.argb(255,238,90,75));
            ImageView status_1 = (ImageView) findViewById(R.id.status_pic1);
            status_1.setImageResource(R.drawable.status_2);
            ImageView status_2 = (ImageView) findViewById(R.id.status_pic2);
            status_2.setImageResource(R.drawable.status_2);
            TextView tx_status = (TextView) findViewById(R.id.status_txt);
            tx_status.setText("NOT PAYED");
        }

        else if(s.getContested()!=true && s.getFullypayed().values().size()==gruppo.getN_partecipanti()-1){
            CardView cd = (CardView) findViewById(R.id.status_card);
            cd.setBackgroundColor(Color.argb(255,135,211,124));
            ImageView status_1 = (ImageView) findViewById(R.id.status_pic1);
            status_1.setImageResource(R.drawable.status_3);
            ImageView status_2 = (ImageView) findViewById(R.id.status_pic2);
            status_2.setImageResource(R.drawable.status_3);
            TextView tx_status = (TextView) findViewById(R.id.status_txt);
            tx_status.setText("FULL PAYED");
        }

        else if(s.getContested()==true && s.getFullypayed().values().size()==gruppo.getN_partecipanti()-1){

            CardView cd = (CardView) findViewById(R.id.status_card);
            cd.setBackgroundColor(Color.argb(255,248,148,6));
            ImageView status_1 = (ImageView) findViewById(R.id.status_pic1);
            status_1.setImageResource(R.drawable.status_1);
            ImageView status_2 = (ImageView) findViewById(R.id.status_pic2);
            status_2.setImageResource(R.drawable.status_1);
            TextView tx_status = (TextView) findViewById(R.id.status_txt);
            tx_status.setText("CONTESTED");
        }


        TextView tv1 = (TextView) findViewById(R.id.compratore_show);
        TextView tv2=(TextView) findViewById(R.id.importo_show);
        TextView tv3=(TextView) findViewById(R.id.data_show);
        TextView tv4=(TextView) findViewById(R.id.part_show);

        tv1.setText(s.getPagante().getName()+" "+ s.getPagante().getSurname());
        String str = String.format("%."+s.getDigit()+"f", s.getImporto());
        tv2.setText(str+" "+s.getValuta());
        tv3.setText(s.getData());
        String str2 = String.format("%."+s.getDigit()+"f", s.getDivisioni().get(SliceAppDB.getUser().getTelephone()).getImporto());
        tv4.setText(str2+" "+s.getValuta());



        Button checkP= (Button) findViewById(R.id.see_division_button);

        checkP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog builder = new Dialog(ExpenseDetails.this);
                builder.setTitle("Price Report");
                builder.setContentView(R.layout.policy_dialog);

                RecyclerView rw = (RecyclerView)builder.findViewById(R.id.policies);
                rw.setHasFixedSize(true);
                RecyclerPolicyAdapter rpa = new RecyclerPolicyAdapter(s.getPolicy(),s.getDivisioni(),s.getDigit(),s.getValuta());
                rw.setAdapter(rpa);

                mLayoutManager = new LinearLayoutManager(ExpenseDetails.this);
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rw.setLayoutManager(mLayoutManager);
                DividerItemDecoration verticalDecoration = new DividerItemDecoration(rw.getContext(), DividerItemDecoration.VERTICAL);
                Drawable verticalDivider = getBaseContext().getDrawable(R.drawable.horizontal_divider);
                verticalDecoration.setDrawable(verticalDivider);
                rw.addItemDecoration(verticalDecoration);





                builder.show();

            }

        });

        Button remove = (Button) findViewById(R.id.remove_btn);

        remove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isRemoved=true;
                groups_ref.child(gruppo.getGroupID()).child("spese").child(s.getExpenseID()).removeEventListener(listener);

                //groups_ref.child(gruppo.getGroupID()).child("removed_exp");
                if (s.getPagante().getTelephone().equals(SliceAppDB.getUser().getTelephone())) {
                    groups_ref.child(gruppo.getGroupID()).child("spese").child(s.getExpenseID()).removeValue();
                    for(final Persona p: gruppo.obtainPartecipanti().values()){

                            String key =user_ref.child(p.getTelephone()).child("amici").child(p.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).push().getKey();
                            if(!p.getTelephone().equals(s.getPagante().getTelephone())) {
                                user_ref.child(p.getTelephone()).child("amici").child(s.getPagante().getTelephone() + ";" + gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s.getDivisioni().get(p.getTelephone()).getImporto());
                                groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s.getPagante().getTelephone()).child("importo").child(key).setValue(s.getDivisioni().get(p.getTelephone()).getImporto());
                                //altri con +
                                //me con il meno
                            }
                            else
                            {
                                for(Persona altri : gruppo.obtainPartecipanti().values()){
                                    if(!altri.getTelephone().equals(s.getPagante().getTelephone())){
                                        user_ref.child(s.getPagante().getTelephone()).child("amici").child(altri.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s.getDivisioni().get(p.getTelephone()).getImporto()*-1);
                                    groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(altri.getTelephone()).child("importo").child(key).setValue(s.getDivisioni().get(p.getTelephone()).getImporto()*-1);
                                }}
                            }

                        String key_s =  groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s.getPagante().getTelephone()).child("importo").push().getKey();
                        groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s.getPagante().getTelephone()).child("importo").child(key_s).setValue(s.getImporto()*-1);


                    }
                    String id_remove = s.getExpenseID();
                    Persona user = SliceAppDB.getUser();
                    Spesa s_r =gruppo.addFake(id_remove,s.getNome_spesa(),s.getImporto(),user.getName()+" "+user.getSurname(),s.getDigit(),s.getValuta());


                    Gson gson = new Gson();
                    Spesa s_r_pojo =gson.fromJson(gson.toJson(s_r),Spesa.class);
                    groups_ref.child(gruppo.getGroupID()).child("spese").child(id_remove).setValue(s_r_pojo);


                    Intent i = new Intent(getBaseContext(),ExpensesActivity.class);
                    i.putExtra("Gruppo",gruppo);
                    startActivity(i);
                    finish();

                    finish();
                        } else {
                        Toast.makeText(getBaseContext(), "Only who bought the item can delete this expense", Toast.LENGTH_LONG).show();
                        }
                        }});



        Button downloadPDF = (Button) findViewById(R.id.download_PDF_button);

        downloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s.getUri()!=null){

                    new downloadPDF(getBaseContext()).execute(s.getUri(),s.getNome_spesa());
                }
                else
                    Toast.makeText(getBaseContext(),"No PDF was uploaded", Toast.LENGTH_LONG).show();
            }
        });

        Button contestExpense = (Button) findViewById(R.id.contest_btn);
        contestExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!s.getContested()){
                    Intent i = new Intent(ExpenseDetails.this, AddContestationActivity.class);
                    i.putExtra("spesa", s.getExpenseID());
                    i.putExtra("gruppo", s.getGruppo());
                    startActivity(i);
                    finish();
                } else {
                    final Intent i = new Intent(ExpenseDetails.this,CommentsExpenseActivity.class);
                    i.putExtra("groupID",s.getGruppo());
                    i.putExtra("expenseID",s.getExpenseID());

                    FirebaseDatabase contestDB = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                    DatabaseReference contestRef = contestDB.getReference().child("groups_prova").child(s.getGruppo())
                                                                            .child("spese").child(s.getExpenseID())
                                                                            .child("contestazioni");

                    contestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            DataSnapshot d = dataSnapshot.getChildren().iterator().next();
                            contestationID = d.getKey();
                            contestator = (String)d.child("phoneNumber").getValue();
                            i.putExtra("contestator",contestator);
                            i.putExtra("contestationID",contestationID);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                   // Toast.makeText(getApplicationContext(), "The expense has been already contested!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        groups_ref.child(gruppo.getGroupID()).child("spese").child(s.getExpenseID()).removeEventListener(listener);
        finish();
    }

    private void zoomImageFromThumb(final View thumbView, Drawable imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
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
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
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
    private void zoomImageFromThumb_res(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageResource(imageResId);

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
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
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
}
