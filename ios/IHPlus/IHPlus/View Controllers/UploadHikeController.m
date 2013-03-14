//
//  UploadHikeController.m
//  IHPlus
//
//  Created by Polina Koronkevich on 4/13/12.
//  Copyright (c) 2012 ecoarttech. All rights reserved.
//

#import "UploadHikeController.h"
#import "UploadHikeDelegate.h"
#import "Constants.h"

@implementation UploadHikeController

@synthesize vcDelegate, hike;
@synthesize hikeName, hikeDesc, userName, scroller, uploadButton;


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
    [hikeName setDelegate:self];
    [hikeDesc setDelegate:self];
    [userName setDelegate:self];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWasShown:)
                                                 name:UIKeyboardDidShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self 
                                             selector:@selector(validateFields:) 
                                                 name:UITextFieldTextDidChangeNotification 
                                               object:hikeName];
    [[NSNotificationCenter defaultCenter] addObserver:self 
                                             selector:@selector(validateFields:) 
                                                 name:UITextFieldTextDidChangeNotification 
                                               object:hikeDesc];
    [[NSNotificationCenter defaultCenter] addObserver:self 
                                             selector:@selector(validateFields:) 
                                                 name:UITextFieldTextDidChangeNotification 
                                               object:userName];
    
    //allows user to hide keyboard.
    UITapGestureRecognizer *singleFingerTap = [[UITapGestureRecognizer alloc] 
                                               initWithTarget:self 
                                               action:@selector(dismissKeyboard:)];
    [scroller addGestureRecognizer:singleFingerTap];
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    self.vcDelegate = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(void)showLoadingDialog
{
    //loading dialog
    if (_loadingIndicator == nil){
        _loadingIndicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        [_loadingIndicator setHidesWhenStopped:YES];
        [_loadingIndicator setBackgroundColor:[UIColor colorWithWhite:0 alpha:.5]];
        _loadingIndicator.frame = CGRectMake(0.0, 0.0, 320.0, [[UIScreen mainScreen] bounds].size.height );
        _loadingIndicator.center = self.view.center;
    }
    [self.scroller addSubview: _loadingIndicator];
    [_loadingIndicator startAnimating];
}

-(void)hideLoadingDialog:(NSString *) error
{
    [_loadingIndicator stopAnimating];
    if (error != nil){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Uploading Hike" message:[NSString stringWithFormat:@"There was an error uploading the hike: %@", error]
                                                       delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
    }
}

#pragma mark - IBActions

-(IBAction)uploadClick:(id)sender
{
    //start dialog
    [self showLoadingDialog];
    // set hike values
    [hike setName:[hikeName text]];
    [hike setDescription:[hikeDesc text]];
    [hike setUsername:[userName text]];
    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@createHike.php", BASE_URL]]];
    NSLog(@"url: %@", req.URL);
    // set POST parameters
    [req setHTTPMethod:@"POST"];
    [req addValue:[NSString stringWithFormat:@"multipart/form-data; charset=UTF-8; boundary=%@", @"####"]
                               forHTTPHeaderField:@"Content-Type"];
    [req setHTTPBody:[hike getUploadData]];
    UploadHikeDelegate *connDelegate = [[UploadHikeDelegate alloc] initWithHandler:^(bool success, NSString *error) {
        NSLog(@"upload handler gets: %i", success);
        if (success){
            NSLog(@"success! delegate? %@", vcDelegate);
            //dismiss loading dialog and modal
            [self hideLoadingDialog:nil];
            [vcDelegate uploadModalController:self done:nil];
            return;
        }
        else{
            NSLog(@"error! %@", error);
            //display error, allow user to re-try uploading
            [self hideLoadingDialog:@"Server was unable to save hike."];
        }
    }];
    
    NSURLConnection *connection =[[NSURLConnection alloc] initWithRequest:req delegate:connDelegate];
    if (!connection) {
        NSLog(@"connection failed");
        [self hideLoadingDialog:@"Could not connect to server"];
    }
}

-(IBAction)cancelClick:(id)sender
{
    [vcDelegate cancelUploadModalController:self];
}

#pragma mark TextField Delegate
CGSize keyboardSize;
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField == hikeName || textField == hikeDesc){
        NSLog(@"name next");
        NSInteger nextTag = textField.tag + 1;
        // Try to find next responder
        UIResponder* nextResponder = [textField.superview viewWithTag:nextTag];
        if (nextResponder) {
            // Found next responder, so set it.
            [nextResponder becomeFirstResponder];
            // Step 3: Scroll the target text field into view.
            CGRect aRect = self.view.frame;
            aRect.size.height -= (keyboardSize.height + 44);
            if (!CGRectContainsPoint(aRect, activeTextField.frame.origin) ) {
                CGPoint scrollPoint = CGPointMake(0.0, activeTextField.frame.origin.y - (keyboardSize.height-15-44));
                [scroller setContentOffset:scrollPoint animated:YES];
            }
        } else {
            // Not found, so remove keyboard.
            [textField resignFirstResponder];
        }
    }
    else if (textField == userName){
        NSLog(@"user done");
        [textField resignFirstResponder];
    }
    return NO;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    activeTextField = textField;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    activeTextField = nil;
}

- (void)validateFields:(NSNotification *)notification
{
    NSLog(@"validating");
    [uploadButton setEnabled:([[userName text] length] > 0 
                              && [[hikeDesc text] length] > 0 
                              && [[hikeName text] length] > 0)];
}

# pragma mark - keyboard handling
- (void)keyboardWasShown:(NSNotification *)notification
{
    
    // Step 1: Get the size of the keyboard.
    keyboardSize = [[[notification userInfo] objectForKey:UIKeyboardFrameBeginUserInfoKey] CGRectValue].size;
    
    NSLog(@"keyboard shown: %f", keyboardSize.height);
    // Step 2: Adjust the bottom content inset of your scroll view by the keyboard height.
    UIEdgeInsets contentInsets = UIEdgeInsetsMake(0.0, 0.0, keyboardSize.height, 0.0);
    scroller.contentInset = contentInsets;
    scroller.scrollIndicatorInsets = contentInsets;
    
    // Step 3: Scroll the target text field into view.
    CGRect aRect = self.view.frame;
    aRect.size.height -= (keyboardSize.height + 44);
    if (!CGRectContainsPoint(aRect, activeTextField.frame.origin) ) {
        CGPoint scrollPoint = CGPointMake(0.0, activeTextField.frame.origin.y - (keyboardSize.height-15-44));
        [scroller setContentOffset:scrollPoint animated:YES];
    }
}

- (void) keyboardWillHide:(NSNotification *)notification {
    UIEdgeInsets contentInsets = UIEdgeInsetsZero;
    scroller.contentInset = contentInsets;
    scroller.scrollIndicatorInsets = contentInsets;
}

- (IBAction)dismissKeyboard:(id)sender
{
    [activeTextField resignFirstResponder];
}

@end
