//
//  HikeDetailsViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Hike.h"

@interface HikeDetailsViewController : UIViewController{
    IBOutlet UILabel *_header;
    IBOutlet UILabel *_desc;
    IBOutlet UILabel *_details;
    IBOutlet UITableView *_table;
    UIActivityIndicatorView *_loadingIndicator;
}

@property (nonatomic, strong) Hike *hike; 

@end
