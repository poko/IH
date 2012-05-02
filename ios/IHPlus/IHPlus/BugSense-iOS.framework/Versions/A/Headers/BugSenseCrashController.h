
/*
 
 BugSenseCrashController.h
 BugSense-iOS
 
 Copyright © 2011, 2012 BugSense Inc.
 
 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation the 
 rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit 
 persons to whom the Software is furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the 
 Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 
 Author: Nick Toumpelis, nick@bugsense.com
 
 */


/**
 The [BugSenseCrashController](BugSenseCrashController) class provides a centralized point of access to BugSense's 
 reporting facilities.
 
 Every application can have exactly one instance of BugSenseCrashController. This instance should be created at the 
 start of the application's lifecycle; typically application:didFinishLaunchingWithOptions:.
 This instance can only be created using one of the parameter-taking sharedInstanceWith... constructors. Thereafter, you
 can access this instance using the sharedInstance method.
 
 The main role of the controller is to send crash reports to the BugSense service. These are sent either immediately
 after the crash, or on relaunch. Typically, crashes are reported with partially symbolicated stack traces, that contain
 the function/method names and program counter offsets. Fully symbolicated stack traces would included the file and
 line for each call in the stack, but obviously this is impossible to take place on the device. Typically, information
 of this kind is recorded in the dSYM bundle (DWARF) during the archiving process (Xcode).
 
 The controller also provides a facility for informing users of updates for the crashing apps. This can be enabled by
 the developer, through the [BugSense Dashboard](http://bugsense.com/). The controller also provides a facility for
 logging exceptions with tags.
 
 Priority is given to the proper execution/crashing of the app, hence, no guarantee is given that all crashes will be 
 reported accurately and at all times. Experience has shown that, meddling with the crashing process may result to all 
 kinds of errors and data corruption. We avoid this by not meddling too much with the app itself. 
 
 Immediate dispatch for apps that are live on the App Store is discouraged; immediate dispatch is not 100% safe. This is
 because it takes place inside the signal handler and it's not async-safe. Immediate dispatch is recommended for beta
 testing only.
 */

#define BUGSENSE_LOG(__EXCEPTION, __TAG) [[BugSenseCrashController sharedInstance] logException:__EXCEPTION withTag:__TAG]

OBJC_EXPORT @interface BugSenseCrashController : NSObject <UIAlertViewDelegate>

/** @name Creating a shared controller instance */

/**
 Creates and returns a singleton crash controller instance with the given API key. If a singleton has already been
 created, this method has no effect.
 
 @param APIKey The BugSense API key
 
 @return A new singleton crash controller with the given API key, or an existing controller with no changes to it (has 
 the API key of its original call).
 */
+ (BugSenseCrashController *) sharedInstanceWithBugSenseAPIKey:(NSString *)APIKey;

/**
 Creates and returns a singleton crash controller instance with the given API key and user dictionary. If a singleton 
 has already been created, this method has no effect.
 
 @param APIKey The BugSense API key
 
 @param userDictionary A dictionary containing custom, user-defined data.
 
 @return A new singleton crash controller with the given values, or an existing controller with no changes to it (has 
 the values of its original call).
 */
+ (BugSenseCrashController *) sharedInstanceWithBugSenseAPIKey:(NSString *)APIKey 
                                                userDictionary:(NSDictionary *)userDictionary;

/**
 Creates and returns a singleton crash controller instance with the given API key, user dictionary and option whether to
 send crash reports immediately or not. If a singleton has already been created, this method has no effect.
 
 @warning This is the designated initializer.
 
 @param APIKey The BugSense API key
 
 @param userDictionary A dictionary containing custom, user-defined data.
 
 @param immediately A value indicating whether the reports should be sent immediately to the service (if YES) or 
 on relaunch (if NO).
 
 @return A new singleton crash controller with the given values, or an existing controller with no changes to it (has 
 the values of its original call).
 */
+ (BugSenseCrashController *) sharedInstanceWithBugSenseAPIKey:(NSString *)APIKey 
                                                userDictionary:(NSDictionary *)userDictionary
                                               sendImmediately:(BOOL)immediately;

/** @name Getting the shared controller instance */

/**
 Returns the singleton crash controller instance.
 
 @warning This method cannot be used to create a new instance. It can only be used to refer to an existing singleton
 crash controller instance, in conjunction with any of the instance methods specified for this class.
 
 @return The existing singleton crash controller instance or nil, if none was found.
 */
+ (BugSenseCrashController *) sharedInstance;

/** @name Logging exceptions */

/**
 Logs asynchronously a given exception to the service, reports its stacktrace and names it with a given tag.
 
 @param exception The exception to log.
 
 @param tag A tag for this exception log.
 
 @return A boolean indicating whether the method completed successfully. This doesn't necessarily mean that the
 exception was logged successfully on the server, only that exception data was generated appropriately and that an
 attempt to send them was made.
 */
- (BOOL) logException:(NSException *)exception withTag:(NSString *)tag;

@end
