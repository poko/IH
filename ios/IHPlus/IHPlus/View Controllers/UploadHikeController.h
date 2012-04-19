//
//  UploadHikeController.h
//  IHPlus
//
//  Created by Polina Koronkevich on 4/13/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Hike.h"

@class UploadHikeController;

@protocol UploadHikeControllerDelegate <NSObject>
- (void)uploadModalController:(UploadHikeController *)controller done:(NSString *) error;
@end


@interface UploadHikeController : UIViewController<UITextFieldDelegate>{
    UITextField *activeTextField;
}

@property (nonatomic, weak) id <UploadHikeControllerDelegate> vcDelegate;
@property (nonatomic, strong) Hike *hike;
@property (nonatomic, strong) IBOutlet UITextField *hikeName;
@property (nonatomic, strong) IBOutlet UITextField *hikeDesc;
@property (nonatomic, strong) IBOutlet UITextField *userName;
@property (nonatomic, strong) IBOutlet UIScrollView *scroller;

-(IBAction)uploadClick:(id)sender;

@end
