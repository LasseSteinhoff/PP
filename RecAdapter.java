package com.example.client;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecAdapter extends RecyclerView.Adapter<RecItem> {

    ArrayList<GenBankEntry> taxon;
    int dotplottaxonindex_1 = -1, dotplottaxonindex_2 = -1;
    boolean switching = true;
    Context context;
    Button buttondotplot;
    ProgressBar progressBar;

    public RecAdapter(Context context, ArrayList<GenBankEntry> taxon, Button buttondotplot, ProgressBar progressBar) {
        this.taxon = taxon;
        this.progressBar = progressBar;
        this.context = context;
        this.buttondotplot = buttondotplot;
    }

    @NonNull
    @Override
    public RecItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecItem holder, int position) {
        GenBankEntry genBankEntry = taxon.get(holder.getAdapterPosition());
        holder.header.setText(genBankEntry.fasta.getHeader());
        holder.def.setText(genBankEntry.getDefinition());
        holder.acc.setText(genBankEntry.getAccession());
        holder.key.setText(genBankEntry.getKeywords());
        holder.org.setText(genBankEntry.getOrganism());
        holder.aut.setText(genBankEntry.getAuthor());
        holder.dna.setText(genBankEntry.fasta.printDNAsubstrings(80));

        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.lay.getVisibility() == View.VISIBLE) {
                    holder.lay.setVisibility(View.GONE);
                }else holder.lay.setVisibility(View.VISIBLE);
            }
        });

        holder.defTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.def.getVisibility() == View.VISIBLE) {
                    holder.def.setVisibility(View.GONE);
                }else holder.def.setVisibility(View.VISIBLE);
            }
        });
        holder.accTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.acc.getVisibility() == View.VISIBLE) {
                    holder.acc.setVisibility(View.GONE);
                }else holder.acc.setVisibility(View.VISIBLE);
            }
        });

        holder.keyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.key.getVisibility() == View.VISIBLE) {
                    holder.key.setVisibility(View.GONE);
                }else holder.key.setVisibility(View.VISIBLE);
            }
        });
        holder.orgTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.org.getVisibility() == View.VISIBLE) {
                    holder.org.setVisibility(View.GONE);
                }else holder.org.setVisibility(View.VISIBLE);
            }
        });
        holder.autTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.aut.getVisibility() == View.VISIBLE) {
                    holder.aut.setVisibility(View.GONE);
                }else holder.aut.setVisibility(View.VISIBLE);
            }
        });
        holder.dnaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.dna.getVisibility() == View.VISIBLE) {
                    holder.dna.setVisibility(View.GONE);
                }else holder.dna.setVisibility(View.VISIBLE);
            }
        });
        holder.header.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(switching) {
                    dotplottaxonindex_1 = holder.getAdapterPosition();
                    switching = !switching;
                    Toast.makeText(context, "Dotplotslot 1", Toast.LENGTH_LONG).show();
                }else {
                    dotplottaxonindex_2 = holder.getAdapterPosition();
                    switching = !switching;
                    Toast.makeText(context, "Dotplotslot 2", Toast.LENGTH_LONG).show();
                }
                if(dotplottaxonindex_1 != -1 & dotplottaxonindex_2 != -1){
                    buttondotplot.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return taxon.size();
    }
}

class RecItem extends RecyclerView.ViewHolder {

    TextView header, def, acc, key, org, aut, dna, defTV, accTV, keyTV, orgTV, autTV, dnaTV;
    LinearLayout lay;

    public RecItem(@NonNull View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.header);
        def = itemView.findViewById(R.id.def);
        acc = itemView.findViewById(R.id.acc);
        key = itemView.findViewById(R.id.key);
        org = itemView.findViewById(R.id.org);
        aut = itemView.findViewById(R.id.aut);
        dna = itemView.findViewById(R.id.dna);
        defTV = itemView.findViewById(R.id.defTV);
        accTV = itemView.findViewById(R.id.accTV);
        keyTV = itemView.findViewById(R.id.keyTV);
        orgTV = itemView.findViewById(R.id.orgTV);
        autTV = itemView.findViewById(R.id.autTV);
        dnaTV = itemView.findViewById(R.id.dnaTV);
        lay = itemView.findViewById(R.id.lay);
    }
}