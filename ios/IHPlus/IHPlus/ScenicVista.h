//
//  ScenicVista.h
//  IHPlus
//
//  Created by Polina Koronkevich on 3/16/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ScenicVista : NSObject

@property (nonatomic, strong) NSString *actionId;
@property (nonatomic, strong) NSString *date;
@property (nonatomic, strong) NSString *lat;
@property (nonatomic, strong) NSString *lng;
@property (nonatomic, strong) NSString *note;
@property (nonatomic, strong) NSString *photoUrl;
// TODO @property (nonatomic, strong) VistaAction *newAction;

+ (ScenicVista *) initWithDictionary:(NSDictionary *) dict;

@end
