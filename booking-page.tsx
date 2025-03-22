import { useState, useEffect } from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { useLocation, useSearch } from "wouter";
import { Service, Appointment } from "@shared/schema";
import { useAuth } from "@/hooks/use-auth";
import { Calendar } from "@/components/ui/calendar";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Loader2, ArrowLeft } from "lucide-react";
import { format } from "date-fns";
import { useToast } from "@/hooks/use-toast";
import { apiRequest, queryClient } from "@/lib/queryClient";
import { ServiceList } from "@/components/skincare/ServiceList";

export default function BookingPage() {
  const { toast } = useToast();
  const search = useSearch();
  const [, navigate] = useLocation();
  const params = new URLSearchParams(search);
  const serviceIdParam = params.get("serviceId");
  
  const [selectedService, setSelectedService] = useState<number | undefined>(
    serviceIdParam ? parseInt(serviceIdParam) : undefined
  );
  const [selectedDate, setSelectedDate] = useState<Date>();
  const [selectedTime, setSelectedTime] = useState<string>();
  const [bookingStep, setBookingStep] = useState<'service' | 'datetime'>(
    serviceIdParam ? 'datetime' : 'service'
  );

  const { data: services, isLoading: loadingServices } = useQuery<Service[]>({
    queryKey: ["/api/services"],
  });

  useEffect(() => {
    if (serviceIdParam) {
      setSelectedService(parseInt(serviceIdParam));
      setBookingStep('datetime');
    }
  }, [serviceIdParam]);

  const bookingMutation = useMutation({
    mutationFn: async (data: { serviceId: number; date: Date }) => {
      const res = await apiRequest("POST", "/api/appointments", data);
      return res.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/appointments"] });
      toast({
        title: "Đặt Lịch Thành Công",
        description: "Lịch hẹn của bạn đã được đặt thành công.",
      });
      navigate("/dashboard");
    },
    onError: (error: Error) => {
      toast({
        title: "Đặt Lịch Thất Bại",
        description: error.message,
        variant: "destructive",
      });
    },
  });

  const timeSlots = [
    "09:00", "10:00", "11:00", "12:00", 
    "14:00", "15:00", "16:00", "17:00"
  ];

  const handleBooking = () => {
    if (!selectedService || !selectedDate || !selectedTime) {
      toast({
        title: "Thông Tin Chưa Đầy Đủ",
        description: "Vui lòng chọn dịch vụ, ngày và giờ.",
        variant: "destructive",
      });
      return;
    }

    const dateTime = new Date(selectedDate);
    const [hours, minutes] = selectedTime.split(":");
    dateTime.setHours(parseInt(hours), parseInt(minutes));

    bookingMutation.mutate({
      serviceId: selectedService,
      date: dateTime,
    });
  };

  const handleServiceSelect = (serviceId: number) => {
    setSelectedService(serviceId);
    setBookingStep('datetime');
  };

  const handleBackToServices = () => {
    setBookingStep('service');
  };

  if (loadingServices) {
    return (
      <div className="h-screen flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }
  
  const currentService = services?.find(s => s.id === selectedService);

  return (
    <div className="min-h-screen p-8 bg-background">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-8">Đặt Lịch Hẹn</h1>
        
        {bookingStep === 'service' ? (
          <div className="space-y-6">
            <h2 className="text-xl font-semibold mb-4">Chọn Dịch Vụ</h2>
            <ServiceList />
          </div>
        ) : (
          <div className="space-y-6">
            <div className="flex items-center gap-4 mb-6">
              <Button variant="outline" size="icon" onClick={handleBackToServices}>
                <ArrowLeft className="h-4 w-4" />
              </Button>
              <h2 className="text-xl font-semibold">Chọn Thời Gian</h2>
            </div>

            {currentService && (
              <Card className="mb-6">
                <CardHeader>
                  <CardTitle>Dịch Vụ Đã Chọn</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="font-medium">{currentService.name}</div>
                  <div className="text-sm text-muted-foreground">
                    {new Intl.NumberFormat('vi-VN', {
                      style: 'currency',
                      currency: 'VND',
                      minimumFractionDigits: 0,
                    }).format(currentService.price)} • {currentService.duration} phút
                  </div>
                </CardContent>
              </Card>
            )}

            <div className="grid md:grid-cols-2 gap-8">
              <Card>
                <CardHeader>
                  <CardTitle>Chọn Ngày</CardTitle>
                </CardHeader>
                <CardContent>
                  <Calendar
                    mode="single"
                    selected={selectedDate}
                    onSelect={setSelectedDate}
                    className="rounded-md border"
                    disabled={(date) => 
                      date < new Date() || date.getDay() === 0
                    }
                  />
                </CardContent>
              </Card>

              {selectedDate && (
                <Card>
                  <CardHeader>
                    <CardTitle>Chọn Giờ</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="grid grid-cols-2 gap-2">
                      {timeSlots.map((time) => (
                        <Button
                          key={time}
                          variant={selectedTime === time ? "default" : "outline"}
                          onClick={() => setSelectedTime(time)}
                          className="w-full"
                        >
                          {time}
                        </Button>
                      ))}
                    </div>
                  </CardContent>
                </Card>
              )}
            </div>

            <div className="mt-8 flex justify-end">
              <Button 
                size="lg"
                onClick={handleBooking}
                disabled={!selectedService || !selectedDate || !selectedTime || bookingMutation.isPending}
              >
                {bookingMutation.isPending ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Đang Đặt Lịch...
                  </>
                ) : (
                  "Xác Nhận Đặt Lịch"
                )}
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
