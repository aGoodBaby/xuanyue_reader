package com.example.fengdeyu.xuanyue_reader.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fengdeyu.xuanyue_reader.R;
import com.example.fengdeyu.xuanyue_reader.activity.ReadActivity;
import com.example.fengdeyu.xuanyue_reader.activity.ReadLocalBookActivity;
import com.example.fengdeyu.xuanyue_reader.activity.ScanBookActivity;
import com.example.fengdeyu.xuanyue_reader.adapter.BookcaseAdapter;
import com.example.fengdeyu.xuanyue_reader.bean.BookItemBean;
import com.example.fengdeyu.xuanyue_reader.other.GetBookCase;
import com.example.fengdeyu.xuanyue_reader.other.GetChapterContent;
import com.example.fengdeyu.xuanyue_reader.other.GetPageAttribute;

/**
 * Created by fengdeyu on 2016/11/23.
 */

public class BookcaseFragment extends Fragment {
    private RecyclerView mRecyclerView;
    BookcaseAdapter bookcaseAdapter;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRecyclerView= (RecyclerView) inflater.inflate(R.layout.bookcase_fragment,container,false);
        return mRecyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        loadTestBook();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        bookcaseAdapter=new BookcaseAdapter(getActivity(), GetBookCase.getInstance().mList,dm.widthPixels);

        mRecyclerView.setAdapter(bookcaseAdapter);

        bookcaseAdapter.setOnItemClickListener(new BookcaseAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GetPageAttribute.getInstance().bookId=position;
                GetPageAttribute.getInstance().source="book_case";
                GetChapterContent.getInstance().currentChapter=GetBookCase.getInstance().mList.get(position).currentChapter;
                GetChapterContent.getInstance().bookTitle=GetBookCase.getInstance().mList.get(position).bookTitle;
                if(GetBookCase.getInstance().mList.get(position).bookAuthor.equals("本地")){
                    GetPageAttribute.getInstance().isChanged=true;
                    startActivity(new Intent(getActivity(), ReadLocalBookActivity.class).putExtra("bookId",position).putExtra("intoWay","bookCase"));
                }else{
                    GetPageAttribute.getInstance().isChanged=true;
                    startActivity(new Intent(getActivity(), ReadActivity.class).putExtra("bookId",position).putExtra("intoWay","bookCase"));
                }
            }


        });

    }


    @Override
    public void onResume() {
        super.onResume();

        if(bookcaseAdapter.getItemCount()==0){
            mRecyclerView.setBackgroundResource(R.mipmap.ic_no_book);
        }else{
            mRecyclerView.setBackgroundResource(R.color.cardview_light_background);
        }

        bookcaseAdapter.notifyDataSetChanged();


    }



    private void loadTestBook(){
        final BookItemBean bookItemBean=new BookItemBean();
        bookItemBean.bookTitle="诡秘之主";
        bookItemBean.bookAuthor="作    者：爱潜水的乌贼";
        bookItemBean.bookContent="最新章节：";
        bookItemBean.bookHref="http://www.ltsw888.com/book_173/";
        bookItemBean.bookIconUrl="http://www.ltsw888.com/files/article/image/0/173/173s.jpg";
        if(!GetBookCase.getInstance().hasBook(bookItemBean)){
            GetBookCase.getInstance().mList.add(bookItemBean);

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    GetBookCase.getInstance().loadChapterContent(0,bookItemBean.bookHref);
                }
            }.start();
        }

    }
}
