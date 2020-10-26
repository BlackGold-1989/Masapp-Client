package com.laodev.masapp.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.ImageDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileImageCell extends LinearLayout {

    private Context context;
    private List<String> mImageUrls;
    private boolean isEditable;
    private ProfileImageCellInterface profileImageCellInterface;

    private List<ConstraintLayout> lst_ctl = new ArrayList<>();
    private List<ImageView> lst_img = new ArrayList<>();

    public ProfileImageCell(Context context, List<String> imgUrls, boolean isEdiable) {
        super(context);

        this.context = context;
        this.mImageUrls = imgUrls;
        this.isEditable = isEdiable;

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_img_profile, this, true);

        initUIView();
    }

    private void initUIView() {
        ConstraintLayout ctl_profile_01 = findViewById(R.id.ctl_profile_01);
        ConstraintLayout ctl_profile_02 = findViewById(R.id.ctl_profile_02);
        ConstraintLayout ctl_profile_03 = findViewById(R.id.ctl_profile_03);
        lst_ctl.add(ctl_profile_01);
        lst_ctl.add(ctl_profile_02);
        lst_ctl.add(ctl_profile_03);

        ImageView img_profile_01 = findViewById(R.id.img_profile_01);
        img_profile_01.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ImageDetailActivity.class);
            intent.putExtra("url", mImageUrls.get(0));
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
            getContext().startActivity(intent, options.toBundle());
        });
        ImageView img_profile_02 = findViewById(R.id.img_profile_02);
        img_profile_02.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ImageDetailActivity.class);
            intent.putExtra("url", mImageUrls.get(1));
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
            getContext().startActivity(intent, options.toBundle());
        });
        ImageView img_profile_03 = findViewById(R.id.img_profile_03);
        img_profile_03.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ImageDetailActivity.class);
            intent.putExtra("url", mImageUrls.get(2));
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
            getContext().startActivity(intent, options.toBundle());
        });

        lst_img.add(img_profile_01);
        lst_img.add(img_profile_02);
        lst_img.add(img_profile_03);

        ImageView img_remove_01 = findViewById(R.id.img_remove_01);
        img_remove_01.setOnClickListener(view -> profileImageCellInterface.onRemove(mImageUrls.get(0)));
        ImageView img_remove_02 = findViewById(R.id.img_remove_02);
        img_remove_02.setOnClickListener(view -> profileImageCellInterface.onRemove(mImageUrls.get(1)));
        ImageView img_remove_03 = findViewById(R.id.img_remove_03);
        img_remove_03.setOnClickListener(view -> profileImageCellInterface.onRemove(mImageUrls.get(2)));

        if (!isEditable) {
            img_remove_01.setVisibility(GONE);
            img_remove_02.setVisibility(GONE);
            img_remove_03.setVisibility(GONE);
        }

        initWithData();
    }

    private void initWithData() {
        for (int i = 0; i < 3; i++) {
            if (i < mImageUrls.size()) {
                lst_ctl.get(i).setVisibility(VISIBLE);
                Picasso.get().load(mImageUrls.get(i))
                        .centerCrop().fit()
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_image)
                        .into(lst_img.get(i), null);
            } else {
                lst_ctl.get(i).setVisibility(INVISIBLE);
            }
        }
    }

    public void setProfileImageCellInterface(ProfileImageCellInterface profileImageCellInterface) {
        this.profileImageCellInterface = profileImageCellInterface;
    }

    public interface ProfileImageCellInterface {
        void onRemove(String url);
    }

}
