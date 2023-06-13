package fr.ensisa.ensiblog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;
import fr.ensisa.ensiblog.models.posts.Content;
import fr.ensisa.ensiblog.models.posts.ContentType;
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.TextContent;

public class AddPostActivity extends AppCompatActivity {

    private final long IMAGE_MAX_SIZE = 1_000_000;

    private final long VIDEO_MAX_SIZE = 10_000_000;

    private static boolean isFull(boolean[] array) {
        for (int i = 0; i < array.length; i++) {
            if (!array[i]) {
                return false;
            }
        }
        return true;
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        LinearLayout list_content = findViewById(R.id.list_content);

        ActivityResultLauncher<PickVisualMediaRequest> pickImage =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        ParcelFileDescriptor parcelFileDescriptor = null;
                        try {
                            parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                        } catch (FileNotFoundException e) {
                            Toast.makeText(AddPostActivity.this,"Error while reading the size of image",Toast.LENGTH_SHORT).show();
                        }
                        if(parcelFileDescriptor.getStatSize() < IMAGE_MAX_SIZE){
                            ImageView imgView = new ImageView(AddPostActivity.this);
                            imgView.setContentDescription(uri.toString());
                            Picasso.get().load(uri).into(imgView);
                            list_content.addView(imgView);
                        } else {
                            Toast.makeText(AddPostActivity.this,"Error, image is too big must be < "+IMAGE_MAX_SIZE,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ActivityResultLauncher<PickVisualMediaRequest> pickVideo =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        ParcelFileDescriptor parcelFileDescriptor = null;
                        try {
                            parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                        } catch (FileNotFoundException e) {
                            Toast.makeText(AddPostActivity.this,"Error while reading the size of video",Toast.LENGTH_SHORT).show();
                        }
                        if(parcelFileDescriptor.getStatSize() < VIDEO_MAX_SIZE){
                            VideoView videoView = new VideoView(AddPostActivity.this);
                            videoView.setVideoURI(uri);
                            Log.i("n6a","URI :"+getFilePathFromUri(uri));
                            videoView.setContentDescription(getFilePathFromUri(uri));
                            MediaController mediaController = new MediaController(AddPostActivity.this);
                            mediaController.setAnchorView(videoView);
                            mediaController.setMediaPlayer(videoView);
                            videoView.setMediaController(mediaController);
                            videoView.setScaleY(1.0f);
                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
                            videoView.setLayoutParams(params2);
                            FrameLayout frameLayout = new FrameLayout(list_content.getContext());
                            frameLayout.setBackgroundResource(R.drawable.round_outline);
                            frameLayout.setClipToOutline(true);
                            frameLayout.addView(videoView);
                            list_content.addView(frameLayout);
                            videoView.start();
                        } else {
                            Toast.makeText(AddPostActivity.this,"Error, video is to big must be < "+VIDEO_MAX_SIZE,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            TextView userName = findViewById(R.id.textViewUsername);
            Email usrEmail = new Email(user.getEmail());
            userName.setText(usrEmail.firstName() + " " + usrEmail.lastName());

            TopicUser topicUser = (TopicUser) extras.get("topicUser");

            Button addImage = findViewById(R.id.buttonAddImage);
            Button addText = findViewById(R.id.buttonAddText);
            Button addVideo = findViewById(R.id.buttonAddVideo);

            addImage.setOnClickListener(v -> {
                pickImage.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            });

            addText.setOnClickListener(v -> {
                EditText editText = new EditText(AddPostActivity.this);
                list_content.addView(editText);
            });

            addVideo.setOnClickListener(v -> {
                pickVideo.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                        .build());
            });

            Button buttonPublish = findViewById(R.id.buttonPublish);

            buttonPublish.setOnClickListener(v -> {

                Log.i("n6a","CLICK");

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                Content[] listContent = new Content[list_content.getChildCount()];

                // Create a list of tasks
                List<UploadTask> tasks = new ArrayList<>();

                for (int i = 0; i < list_content.getChildCount(); i++) {
                    View element = list_content.getChildAt(i);
                    Log.i("n6a","content ??");
                    if (element instanceof TextView) {
                        listContent[i] = new Content(ContentType.TEXT, ((TextView) element).getText().toString());
                    } else if (element instanceof ImageView) {
                        StorageReference ref = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");
                        ((ImageView) element).setDrawingCacheEnabled(true);
                        ((ImageView) element).buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) ((ImageView) element).getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                        byte[] data = baos.toByteArray();
                        tasks.add(ref.putBytes(data));
                        listContent[i] = new Content();
                        listContent[i].setType(ContentType.IMAGE.getType());

                    } else if (element instanceof FrameLayout) {
                        String uri = (String) ((VideoView)((FrameLayout) element).getChildAt(0)).getContentDescription();
                        StorageReference ref = storageRef.child("videos/" + UUID.randomUUID().toString() + ".mp4");
                        tasks.add(ref.putFile(Uri.fromFile(new File(uri))));
                        listContent[i] = new Content();
                        listContent[i].setType(ContentType.VIDEO.getType());
                    }
                }
                boolean[] urls = new boolean[tasks.size()];

                int j = 0;
                for (int i = 0; i < listContent.length; i++) {
                    if(listContent[i].getType() != ContentType.TEXT.getType()){
                        int finalJ = j;
                        int finalI = i;
                        tasks.get(j).continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(AddPostActivity.this, "Error while uploading video", Toast.LENGTH_SHORT).show();
                            } else {
                                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                    listContent[finalI].setData(downloadUrl.toString());
                                    urls[finalJ] = true;
                                    if(isFull(urls)) {
                                        Post newPost = new Post(new Date(), topicUser.getTopic(), topicUser.getUser(), Arrays.asList(listContent), new Date());
                                        Database.getInstance().add(Table.POSTS.getName(), newPost, Post.class).addOnCompleteListener(task2 -> {
                                            if(task2.isSuccessful()){
                                                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                                intent.putExtra("user",user);
                                                startActivity(intent);
                                            } else Toast.makeText(AddPostActivity.this, "Erreur lors de la publication du post", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            }
                            return null;
                        });
                        j++;
                    }
                }
            });
        }
    }
}