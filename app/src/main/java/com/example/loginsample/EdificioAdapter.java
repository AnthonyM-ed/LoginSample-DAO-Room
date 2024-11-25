package com.example.loginsample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginsample.model.dao.EdificioDao;
import com.example.loginsample.model.ent.EdificioEntity;

import java.util.List;

public class EdificioAdapter extends RecyclerView.Adapter<EdificioAdapter.EdificioViewHolder> {

    private List<EdificioEntity> edificioList;
    private OnEdificioClickListener onEdificioClickListener;

    // Interfaz para manejar clics en los elementos
    public interface OnEdificioClickListener {
        void onEdificioClick(EdificioEntity edificio);
    }

    // Constructor
    public EdificioAdapter(List<EdificioEntity> edificioList, OnEdificioClickListener onEdificioClickListener) {
        this.edificioList = edificioList;
        this.onEdificioClickListener = onEdificioClickListener;
    }

    @NonNull
    @Override
    public EdificioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item para cada edificio
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_building, parent, false);
        return new EdificioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EdificioViewHolder holder, int position) {
        EdificioEntity edificio = edificioList.get(position);
        holder.title.setText(edificio.getEdiName());
        holder.description.setText(edificio.getDescription());

        // Cargar la imagen desde el nombre de la imagen
        int imageResId = edificio.getEdiImagen();
        holder.image.setImageResource(imageResId);

        // Manejar el clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            if (onEdificioClickListener != null) {
                onEdificioClickListener.onEdificioClick(edificio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return edificioList.size();
    }

    // Vista interna del ViewHolder para cada elemento
    public static class EdificioViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;

        public EdificioViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.building_title);
            description = itemView.findViewById(R.id.building_description);
            image = itemView.findViewById(R.id.building_image);
        }
    }
}
