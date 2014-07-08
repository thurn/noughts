#import <Foundation/Foundation.h>

#import "AnalyticsService.h"

@interface AnalyticsServiceImpl : NSObject <NTSAnalyticsService>

- (void)trackEventWithNSString:(NSString *)name;

- (void)trackEventDimensionsWithNSString:(NSString *)name
                         withJavaUtilMap:(id<JavaUtilMap>)dimensions;

@end
