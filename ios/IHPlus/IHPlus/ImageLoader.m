//
//  ImageLoader.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/12/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ImageLoader.h"

@implementation ImageLoader

static NSMutableDictionary *images;

+(UIImage *) getImageForUrl:(NSString *)url
{
    if (images == nil){ //create images cache
        images = [[NSMutableDictionary alloc] init];
    }
    if (![images objectForKey:url]){
        NSLog(@"adding image %@ to cache.", url);
        // load image
        UIImage *img = [UIImage imageWithData: [NSData dataWithContentsOfURL:[NSURL URLWithString:url]]];
        NSLog(@"image isn't nulL?? %@", img);
        [images setValue:img forKey:url];
    }
    NSLog(@"returning image %@ ", [images objectForKey:url]);
    return [images objectForKey:url];
}

@end
