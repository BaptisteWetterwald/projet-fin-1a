package fr.ensisa.ensiblog;

import static fr.ensisa.ensiblog.Utils.getFilePathFromUri;

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


import com.bumptech.glide.Glide;
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

/**
 * Activity used on the AddPost page of the application
 **/
public class AddPostActivity extends AppCompatActivity {

    private final long IMAGE_MAX_SIZE = 1_000_000;
    private final long VIDEO_MAX_SIZE = 10_000_000;
    private final String IMAGE_MAX_SIZE_STRING = "1Mo";
    private final String VIDEO_MAX_SIZE_STRING = "10Mo";
    private Topic currentTopic = null;

    /**
     * check if there is a boolean false in an array of boolean
     @param array : the array to check
     **/
    private static boolean isFull(boolean[] array) {
        for (int i = 0; i < array.length; i++) {
            if (!array[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Function called when AddPostActivity starts
     **/
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
                            //Picasso.get().load(uri).into(imgView);
                            if (uri.toString().contains(".gif"))
                                Glide.with(AddPostActivity.this).asGif().load(uri.toString()).into(imgView);
                            else
                                Glide.with(AddPostActivity.this).load(uri.toString()).into(imgView);
                            list_content.addView(imgView);
                        } else {
                            Toast.makeText(AddPostActivity.this,"Error, image is too big must be < "+IMAGE_MAX_SIZE_STRING,Toast.LENGTH_SHORT).show();
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
                            videoView.setContentDescription(getFilePathFromUri(getContentResolver(),uri));
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
                            Toast.makeText(AddPostActivity.this,"Error, video is to big must be < "+VIDEO_MAX_SIZE_STRING,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            User userModel = (User) extras.get("User");
            TextView userName = findViewById(R.id.textViewUsername);
            Email usrEmail = new Email(user.getEmail());
            userName.setText(usrEmail.firstName() + " " + usrEmail.lastName());

            ImageView img =  findViewById(R.id.imageViewUserPicture);
            if(userModel.getPhotoUrl() != null)
                Glide.with(AddPostActivity.this).load(userModel.getPhotoUrl()).into(img);

            // On récupère la liste des topics auquel l'user est abonné
            Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{userModel}).addOnSuccessListener(topics -> {
                List<Button> buttons = new ArrayList<>();
                if (topics.size() > 0) {
                    LinearLayout themesBar = findViewById(R.id.theme_bar);
                    themesBar.removeAllViews();
                    for (int i = 0; i < topics.size(); i++) {
                        if (topics.get(i).getRole().getRole() >= 2) {
                            Button button = new Button(this);
                            TopicUser btnTopic = topics.get(i);
                            button.setText(btnTopic.getTopic().getName());
                            button.setOnClickListener(v -> {
                                currentTopic=btnTopic.getTopic();
                                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#66A0B1")));
                                for (Button otherButton : buttons) {
                                    if (otherButton != button) {
                                        otherButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#444444"))); // Change to desired color
                                    }
                                }
                            });
                            themesBar.addView(button);
                            buttons.add(button);
                        }
                    }
                }
            });
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
                editText.setTextColor(Color.rgb(0,0,0));
                list_content.addView(editText);
                editText.requestFocus();
            });
            addVideo.setOnClickListener(v -> {
                pickVideo.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                        .build());
            });
            Button buttonPublish = findViewById(R.id.buttonPublish);
            buttonPublish.setOnClickListener(v -> {
                if(currentTopic != null){
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
                            String uri = (String) element.getContentDescription();
                            String path = getFilePathFromUri(getContentResolver(), Uri.parse(uri));
                            StorageReference ref = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");
                            if(path.contains(".gif")) ref = storageRef.child("images/" + UUID.randomUUID().toString() + ".gif");

                            tasks.add(ref.putFile(Uri.fromFile(new File(path))));
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
                    if(!tasks.isEmpty()){
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
                                                Post newPost = new Post(new Date(), currentTopic, userModel, Arrays.asList(listContent), new Date());
                                                Database.getInstance().add(Table.POSTS.getName(), newPost, Post.class).addOnCompleteListener(task2 -> {
                                                    if(task2.isSuccessful()){
                                                        Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                                        intent.putExtra("user",user);
                                                        //intent.putExtra("User",userModel);
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
                    } else {
                        Post newPost = new Post(new Date(), currentTopic, userModel, Arrays.asList(listContent), new Date());
                        Database.getInstance().add(Table.POSTS.getName(), newPost, Post.class).addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful()){
                                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                intent.putExtra("user",user);
                                startActivity(intent);
                            } else Toast.makeText(AddPostActivity.this, "Erreur lors de la publication du post", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else Toast.makeText(AddPostActivity.this, "Veuillez choisir un thème dans lequel publier votre post", Toast.LENGTH_SHORT).show();
            });
        }
    }
}