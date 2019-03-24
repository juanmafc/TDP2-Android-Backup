package fiubatdp2g1_hoycomo.hoycomo.service;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImagesLoader {


    public static void Load(String imageUrl, ImageView imageView) {
        if ( ( imageUrl.isEmpty() ) || (imageUrl == null) ) {
            //TODO: default / error image
        }
        else {
            Picasso.get().load(imageUrl).into(imageView);
        }
    }
}
