package me.heesu.hackingspringbootch2reactive.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

import java.util.Date;

@Getter
@Setter
public class Item {
    private @Id String id; // xxx: 몽고DB의 ObjectId 값으로 사용 명시
    private String name;
    private String description;
    private double price;
    private String distributorRegion;
    private Date releaseDate;
    private int availableUnits;
    private Point location;
    private boolean active;

    private Item(){ }

    public Item(String name, String description, double price){
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Item(String id, String name, String description, double price){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj){
        Item target = (Item)obj;

        if(target == null) {
            return false;
        }

        if(StringUtils.defaultString(this.getId(),"").equals(target.getId())
            && StringUtils.defaultString(this.getName(),"").equals(target.getName())
            && StringUtils.defaultString(this.getDescription(),"").equals(target.getDescription())
            && this.getPrice() == target.getPrice()){

            return true;
        }else {
            return false;
        }
    }
}
