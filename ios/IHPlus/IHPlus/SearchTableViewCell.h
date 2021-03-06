//
//  SearchTableViewCell.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SearchTableViewCell : UITableViewCell

@property (nonatomic, strong) IBOutlet UILabel *name;
@property (nonatomic, strong) IBOutlet UILabel *description;
@property (nonatomic, strong) IBOutlet UIButton *viewButton;
@property (nonatomic, strong) IBOutlet UIImageView *imgView;

-(void) setIcon:(BOOL) companion;

@end
