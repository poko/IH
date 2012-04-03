//
//  HikeDetailsViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Hike.h"

@interface HikeDetailsViewController : UIViewController <UITableViewDelegate>{
    IBOutlet UILabel *_header;
    IBOutlet UILabel *_desc;
    IBOutlet UILabel *_details;
}

@property (nonatomic, strong) Hike *hike; 

@end
