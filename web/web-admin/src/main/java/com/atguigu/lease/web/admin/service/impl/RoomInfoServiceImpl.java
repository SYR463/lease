package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.ibatis.ognl.CollectionElementsAccessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private RoomAttrValueService roomAttrValueService;

    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private RoomLeaseTermService roomLeaseTermService;

    @Override
    public void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {

        boolean isUpdate = roomSubmitVo.getId() != null;

        // 更新房间信息表
        super.saveOrUpdate(roomSubmitVo);

        // 如果需要更新房间信息，先删除，后插入
        if(isUpdate) {
            // 图片列表
            LambdaQueryWrapper<GraphInfo> graphVoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            graphVoLambdaQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphVoLambdaQueryWrapper.eq(GraphInfo::getItemId, roomSubmitVo.getId());
            graphInfoService.remove(graphVoLambdaQueryWrapper);

            // 属性信息列表
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomAttrValueLambdaQueryWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            roomAttrValueService.remove(roomAttrValueLambdaQueryWrapper);

            // 配套信息列表
            LambdaQueryWrapper<RoomFacility> roomFacilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomFacilityLambdaQueryWrapper.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
            roomFacilityService.remove(roomFacilityLambdaQueryWrapper);

            // 标签信息列表
            LambdaQueryWrapper<RoomLabel> roomLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomLabelLambdaQueryWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
            roomLabelService.remove(roomLabelLambdaQueryWrapper);


            // 支付方式列表
            LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomPaymentTypeLambdaQueryWrapper.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
            roomPaymentTypeService.remove(roomPaymentTypeLambdaQueryWrapper);

            // 可选租期列表
            LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roomLeaseTermLambdaQueryWrapper.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
            roomLeaseTermService.remove(roomLeaseTermLambdaQueryWrapper);
        }


        // 图片列表，从roomSubmitVo中获取图片列表graphVoList，创建数组graphInfoArrayList，依次将graphVoList中数据存入graphInfoArrayList
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)) {
            ArrayList<GraphInfo> graphInfoArrayList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();

                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setName(graphVo.getName());
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfo.setItemType(ItemType.ROOM);

                graphInfoArrayList.add(graphInfo);
            }

            graphInfoService.saveBatch(graphInfoArrayList);
        }



        // 属性信息列表
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();

        if(!CollectionUtils.isEmpty(attrValueIds)) {
            ArrayList<RoomAttrValue> roomAttrValueArrayList = new ArrayList<>();
            for (Long attrValueId : attrValueIds) {
                RoomAttrValue roomAttrValue = RoomAttrValue.builder().roomId(roomSubmitVo.getId()).attrValueId(attrValueId).build();
                roomAttrValueArrayList.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValueArrayList);
        }


        // 配套信息列表
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();

        if(!CollectionUtils.isEmpty(facilityInfoIds)) {
            ArrayList<RoomFacility> facilityInfoArrayList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                RoomFacility roomFacility = RoomFacility.builder().roomId(roomSubmitVo.getId()).facilityId(facilityInfoId).build();
                facilityInfoArrayList.add(roomFacility);
            }
            roomFacilityService.saveBatch(facilityInfoArrayList);
        }


        // 标签信息列表
        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();

        if(!CollectionUtils.isEmpty(labelInfoIds)) {
            ArrayList<RoomLabel> roomLabelArrayList = new ArrayList<>();
            for (Long labelInfoId : labelInfoIds) {
                RoomLabel roomLabel = RoomLabel.builder().roomId(roomSubmitVo.getId()).labelId(labelInfoId).build();
                roomLabelArrayList.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabelArrayList);
        }

        // 支付方式列表
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if(!CollectionUtils.isEmpty(paymentTypeIds)) {
            ArrayList<RoomPaymentType> roomPaymentTypeArrayList = new ArrayList<>();
            for (Long paymentTypeId : paymentTypeIds) {
                RoomPaymentType roomPaymentType = RoomPaymentType.builder().roomId(roomSubmitVo.getId()).paymentTypeId(paymentTypeId).build();
                roomPaymentTypeArrayList.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(roomPaymentTypeArrayList);
        }

        // 可选租期列表
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();

        if(!CollectionUtils.isEmpty(leaseTermIds)) {
            ArrayList<RoomLeaseTerm> roomLeaseTerms = new ArrayList<>();
            for (Long leaseTermId : leaseTermIds) {
                RoomLeaseTerm roomLeaseTerm = RoomLeaseTerm.builder().roomId(roomSubmitVo.getId()).leaseTermId(leaseTermId).build();
                roomLeaseTerms.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(roomLeaseTerms);
        }
    }

    @Override
    public IPage<RoomItemVo> pageRoomItemByQuery(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        return roomInfoMapper.pageRoomItemByQuery(page, queryVo);
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        // 查找相关的属性值
        RoomInfo roomInfo = roomInfoMapper.selectById(id);

        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(roomInfo.getApartmentId());

        List<GraphVo> graphVoList = graphInfoMapper.selectByItemTypeAndId(ItemType.ROOM, roomInfo.getApartmentId());

        List<AttrValueVo> attrvalueVoList = attrValueMapper.selectListByRoomid(id);

        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);

        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);

        List<PaymentType> paymentTypeList = paymentTypeMapper.selectListByRoomId(id);

        List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);

        // 拼接： 将查询出的属性值填充到 roomDetailVo 中
        RoomDetailVo roomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, roomDetailVo);

        roomDetailVo.setApartmentInfo(apartmentInfo);
        roomDetailVo.setGraphVoList(graphVoList);
        roomDetailVo.setAttrValueVoList(attrvalueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        roomDetailVo.setLeaseTermList(leaseTermList);

        return roomDetailVo;
    }

    /**
     * 使用房间id，删除房间信息
     * @param id
     */
    @Override
    public void removeRoomById(Long id) {
        // 删除房间自身携带的信息
        super.removeById(id);

        // 删除与房间相关的其他信息
        LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getId, id);
        graphInfoService.remove(graphInfoLambdaQueryWrapper);

        LambdaQueryWrapper<RoomAttrValue> roomAttrValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomAttrValueLambdaQueryWrapper.eq(RoomAttrValue::getRoomId, id);
        roomAttrValueService.remove(roomAttrValueLambdaQueryWrapper);

        LambdaQueryWrapper<RoomFacility> roomFacilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomFacilityLambdaQueryWrapper.eq(RoomFacility::getRoomId, id);
        roomFacilityService.remove(roomFacilityLambdaQueryWrapper);

        LambdaQueryWrapper<RoomLabel> roomLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomLabelLambdaQueryWrapper.eq(RoomLabel::getRoomId, id);
        roomLabelService.remove(roomLabelLambdaQueryWrapper);

        LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomPaymentTypeLambdaQueryWrapper.eq(RoomPaymentType::getRoomId, id);
        roomPaymentTypeService.remove(roomPaymentTypeLambdaQueryWrapper);

        LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomLeaseTermLambdaQueryWrapper.eq(RoomLeaseTerm::getRoomId, id);
        roomLeaseTermService.remove(roomLeaseTermLambdaQueryWrapper);

    }
}




