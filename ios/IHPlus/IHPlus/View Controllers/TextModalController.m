//
//  TextModalController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/11/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "TextModalController.h"

@implementation TextModalController

@synthesize textButton, disclaimer;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

-(IBAction)textClick:(id)sender
{
    if (![MFMessageComposeViewController canSendText]){
        //we have a problem! 
        NSLog(@"can't send texts :(");
        [disclaimer setText:@"your device can't send text messages. hit 'done' to record the field note."];
    }
    else{
        MFMessageComposeViewController *textMsg = [[MFMessageComposeViewController alloc] init];
        [textMsg setMessageComposeDelegate:self];
        [textMsg setBody:[[self input] text]];
        [self presentModalViewController:textMsg animated:YES];
    }
}

- (void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result
{
    [[self vcDelegate] noteModalController:self done:[[self input] text]];
}

- (void)textViewDidChange:(UITextView *)textView
{
    NSLog(@"editing text");
    [super textViewDidChange:textView];
    [textButton setEnabled:[[textView text] length] > 0];
}

@end
