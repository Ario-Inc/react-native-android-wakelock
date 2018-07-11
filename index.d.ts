// Type definitions for [~THE LIBRARY NAME~] [~OPTIONAL VERSION NUMBER~]
// Project: [~THE PROJECT NAME~]
// Definitions by: [~YOUR NAME~] <[~A URL FOR YOU~]>

export as namespace AndroidWakeLockModule;

export function acquireWakeLock(screenOn: boolean): Promise<boolean>;
export function releaseWakeLock(): Promise<boolean>;