//
//  SearchTableViewCell.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import "SearchTableViewCell.h"

@implementation SearchTableViewCell

@synthesize name,description, viewButton, imgView;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void) setIcon:(BOOL)companion
{
    [name sizeToFit];
    NSString *fileName = companion ? @"companion_search_result_img.png" : @"standard_search_result_img.png";
    CGRect titleFrame = [name frame];
    CGRect frame = CGRectMake(titleFrame.size.width + 15, titleFrame.origin.y + 5 , 12, 16);
    UIImage *icon = [UIImage imageNamed:fileName];
    [imgView setFrame: frame];
    [imgView setImage:icon];
    [self.contentView addSubview:imgView];
}

@end
