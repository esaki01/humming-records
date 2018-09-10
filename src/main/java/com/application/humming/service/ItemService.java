package com.application.humming.service;

import java.util.List;

import com.application.humming.dto.ItemDto;
import com.application.humming.exception.HummingException;

import lombok.NonNull;

public interface ItemService {

    /**
     * 初期表示のアイテム一覧を取得する.
     *
     * @param Integer offset
     * @param Integer limit
     * @return List<ItemEntity>
     * @throws HummingException
     */
    public List<ItemDto> getInitialItemList(@NonNull final Integer offset, @NonNull final Integer limit) throws HummingException;

    /**
     * アイテム詳細を取得する.
     *
     * @param Integer id
     * @return ItemEntity
     */
    public ItemDto getItemDetail(@NonNull final Integer id);

    /**
     * 歌手名または曲名でアイテムを取得する.
     *
     * @param String singerOrSong
     * @return List<ItemEntity>
     */
    public List<ItemDto> getItemWithSingerOrSong(@NonNull final String singerOrSong);
}