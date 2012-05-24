//
//  SearchTableViewCell.m
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SearchTableViewCell.h"

@implementation SearchTableViewCell

@synthesize name,description, viewButton;

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
    CGRect titleFrame = [name frame];
    //CGRectMake(0, 0, 100, 100)
    UIImage *icon = [UIImage imageWithContentsOfFile:@"scenic_vista_point.png"];
    UIImageView* imgView = [[UIImageView alloc] initWithFrame:titleFrame];
    [imgView setImage:icon];
    [self addSubview:imgView];
}

@end
