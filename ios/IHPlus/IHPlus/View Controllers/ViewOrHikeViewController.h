//
//  ViewOrHikeViewController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Hike.h"

@interface ViewOrHikeViewController : UIViewController{
    IBOutlet UILabel *label;
}

@property (nonatomic, strong) Hike *hike; 


@end
