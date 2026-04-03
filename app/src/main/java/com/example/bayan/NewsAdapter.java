    package com.example.bayan;
    import android.content.Intent;
    import android.graphics.Color;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;
    import java.util.TimeZone;

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

        private List<News> newsList;


        public NewsAdapter(List<News> newsList) {
            this.newsList = newsList;
        }

        @NonNull
        @Override
        public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news, parent, false);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

            News news = newsList.get(position);
            holder.title.setText(news.getTitle());
            holder.description.setText(news.getDescription());
            holder.categorie.setText(news.getCategorie());
            holder.date.setText(formatDate(news.getCreatedAt()));


            holder.itemView.setOnClickListener(v -> {
                holder.mainCard.setCardBackgroundColor(Color.parseColor("#F2F2F7"));
                holder.mainCard.setStrokeColor(Color.TRANSPARENT);
                holder.title.setTextColor(Color.parseColor("#8E8E93")); //Gris title
                holder.description.setAlpha(0.5f); //description changer

                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("title", news.getTitle());
                intent.putExtra("description", news.getDescription());
                intent.putExtra("category", news.getCategorie());
                intent.putExtra("date", formatDate(news.getCreatedAt()));

                v.getContext().startActivity(intent);
            });
        }
        private String formatDate(String rawDate) {
            try {
                //Parse Supabase timestamp format
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault());
                input.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = input.parse(rawDate);

                //format: "01 Apr 2026 • 14:30"
                SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault());
                return output.format(date);
            } catch (ParseException e) {
                return rawDate; //fallback to raw if parsing fails
            }
        }
        @Override
        public int getItemCount() { return newsList.size(); }

        static class NewsViewHolder extends RecyclerView.ViewHolder {
            com.google.android.material.card.MaterialCardView mainCard;
            TextView title, description, categorie, date;

            public NewsViewHolder(@NonNull View itemView) {
                super(itemView);
                mainCard = itemView.findViewById(R.id.mainCard);

                title = itemView.findViewById(R.id.tvTitle);
                description = itemView.findViewById(R.id.tvDescription);
                categorie = itemView.findViewById(R.id.tvCategorie);
                date = itemView.findViewById(R.id.tvDate);
            }
        }

    }